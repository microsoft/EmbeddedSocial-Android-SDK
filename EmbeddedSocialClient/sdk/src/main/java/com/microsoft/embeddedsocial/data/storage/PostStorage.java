/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.microsoft.embeddedsocial.account.UserAccount;
import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.base.expression.Template;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.data.model.AddPostData;
import com.microsoft.embeddedsocial.data.model.DiscussionItem;
import com.microsoft.embeddedsocial.data.storage.syncadapter.EditedTopicSyncAdapter;
import com.microsoft.embeddedsocial.data.storage.syncadapter.PostSyncAdapter;
import com.microsoft.embeddedsocial.data.storage.transaction.DbTransaction;
import com.microsoft.embeddedsocial.server.model.view.CommentView;
import com.microsoft.embeddedsocial.server.model.view.TopicView;
import com.microsoft.embeddedsocial.autorest.models.BlobType;
import com.microsoft.embeddedsocial.autorest.models.ContentType;
import com.microsoft.embeddedsocial.autorest.models.PublisherType;
import com.microsoft.embeddedsocial.data.storage.exception.FatalDatabaseException;
import com.microsoft.embeddedsocial.data.storage.model.EditedTopic;
import com.microsoft.embeddedsocial.data.storage.syncadapter.DiscussionItemSyncAdapter;
import com.microsoft.embeddedsocial.server.model.view.ReplyView;
import com.microsoft.embeddedsocial.server.sync.ISynchronizable;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stores user-created posts for uploading to the server.
 */
public class PostStorage {

	private static final String ON_UNSENT_COMMENT_REMOVED_STATEMENT = new Template(
		"update ${topics} set ${comment_count} = ${comment_count} - 1 where ${t_handle} = ?")
		.var("topics", DbSchemas.Topics.TABLE_NAME)
		.var("comment_count", DbSchemas.Topics.TOTAL_COMMENTS)
		.var("t_handle", DbSchemas.Topics.TOPIC_HANDLE)
		.render();

	private static final String ON_UNSENT_REPLY_REMOVED_STATEMENT = new Template(
		"update ${comments} set ${reply_count} = ${reply_count} - 1 where ${c_handle} = ?")
		.var("comments", DbSchemas.Comments.TABLE_NAME)
		.var("reply_count", DbSchemas.Comments.TOTAL_REPLIES)
		.var("c_handle", DbSchemas.Comments.COMMENT_HANDLE)
		.render();

	private final ImageStorage imageStorage;
	private final Dao<AddPostData, Integer> postDao;
	private final Dao<TopicView, String> topicDao;
	private final Dao<CommentView, String> commentDao;
	private Dao<DiscussionItem, Integer> discussionItemDao;
	private Dao<EditedTopic, Integer> editedTopicDao;

	/**
	 * Creates an instance.
	 * @param context   valid context
	 */
	public PostStorage(Context context) {
		imageStorage = new ImageStorage(context);
		DatabaseHelper helper = GlobalObjectRegistry.getObject(DatabaseHelper.class);
		postDao = helper.getPostDao();
		topicDao = helper.getTopicDao();
		commentDao = helper.getCommentDao();
		try {
			discussionItemDao = helper.getDao(DiscussionItem.class);
			editedTopicDao = helper.getDao(EditedTopic.class);
		} catch (SQLException e) {
			throw new FatalDatabaseException(e);
		}
	}

	/**
	 * Stores a new discussion item.
	 * @param item  the item to store
	 * @throws SQLException if cache fails.
	 */
	public void storeDiscussionItem(DiscussionItem item) throws SQLException {
		DbTransaction.performTransaction(discussionItemDao, () -> discussionItemDao.create(item));
	}

	/**
	 * Stores a new post.
	 * @param title			post title
	 * @param description	post description
	 * @param imageUri      URI of post image, if image is included
	 * @return	<code>true</code> if post is stored successfully.
	 */
	public boolean storePost(String title, String description, @Nullable Uri imageUri,
							 PublisherType publisherType) {
		String imagePath;

		try {
			imagePath = storeImageToTempFile(imageUri);
			return storePostData(title, description, imagePath, publisherType);
		} catch (IOException | SQLException e) {
			DebugLog.logException(e);
			return false;
		}
	}

	private boolean storePostData(String title, String description, String imagePath,
								  PublisherType publisherType)
		throws SQLException {

		boolean result;

		AddPostData postData = new AddPostData(title, description, imagePath, publisherType);
		if (postDao.create(postData) > 0) {
			result = true;
			DebugLog.i("stored a post for sending");
		} else {
			result = false;
			if (!imagePath.isEmpty()) {
				deleteSafely(imagePath);
			}
		}

		return result;
	}

	private void deleteSafely(String imagePath) {
		boolean deleted = new File(imagePath).delete();
		if (!deleted) {
			DebugLog.e("couldn't delete image " + imagePath);
		}
	}

	public String storeImageToTempFile(@Nullable Uri imageUri) throws IOException {
		String imagePath = "";

		if (imageUri != null) {
			ImageStorage.StoredImage storedImage = imageStorage.storeImage(imageUri);
			imagePath = storedImage.getImagePath();
			DebugLog.i("image size: " + new File(imagePath).length() / 1024 + "K");
		}

		return imagePath;
	}

	/**
	 * Gets synchronizables for pending posts.
	 * @return  list of {@linkplain ISynchronizable}
	 */
	public List<ISynchronizable> getPendingPosts() {
		List<ISynchronizable> result = new ArrayList<>();
		List<AddPostData> posts = getAllPendingPosts();

		for (AddPostData post : posts) {
			result.add(new PostSyncAdapter(this, post));
		}

		return result;
	}

	/**
	 * Gets synchronizables for pending discussion items.
	 * @return  list of {@linkplain ISynchronizable}
	 */
	public List<ISynchronizable> getPendingDiscussionItems() {
		List<DiscussionItem> data;
		try {
			data = discussionItemDao.queryForAll();
		} catch (SQLException e) {
			data = Collections.emptyList();
		}
		List<ISynchronizable> result = new ArrayList<>();

		for (DiscussionItem item : data) {
			result.add(new DiscussionItemSyncAdapter(item, discussionItemDao));
		}

		return result;
	}

	/**
	 * Gets synchronizables for pending edited topics.
	 * @return  list of {@linkplain ISynchronizable}
	 */
	public List<ISynchronizable> getPendingEditedTopics() {
		List<EditedTopic> editedTopics;

		try {
			editedTopics = editedTopicDao.queryForAll();
		} catch (SQLException e) {
			editedTopics = Collections.emptyList();
		}

		List<ISynchronizable> result = new ArrayList<>();
		for (EditedTopic topic : editedTopics) {
			result.add(new EditedTopicSyncAdapter(topic, editedTopicDao));
		}

		return result;
	}

	/**
	 * Gets all topic comments that are waiting for synchronization.
	 * @param   topicHandle   handle of the topic
	 * @return  all topic comments.
	 * @throws SQLException if the database fails.
	 */
	public List<CommentView> getUnsentComments(String topicHandle) throws SQLException {
		List<DiscussionItem> queryResult = getDiscussionItems(topicHandle, ContentType.COMMENT);
		List<CommentView> comments = new ArrayList<>();
		for (DiscussionItem item : queryResult) {
			comments.add(item.asComment());
		}
		return comments;
	}

	/**
	 * Gets all comment replies that are waiting for synchronization.
	 * @param   commentHandle   handle of the comment
	 * @return  all comment replies.
	 * @throws SQLException if the database fails.
	 */
	public List<ReplyView> getUnsentReplies(String commentHandle) throws SQLException {
		List<DiscussionItem> queryResult = getDiscussionItems(commentHandle, ContentType.REPLY);
		List<ReplyView> replies = new ArrayList<>();
		for (DiscussionItem item : queryResult) {
			replies.add(item.asReply());
		}
		return replies;
	}

	/**
	 * Stores a topic that was edited.
	 * @param editedTopic   the topic to store
	 * @throws SQLException if the database fails
	 */
	public void storeEditedTopic(EditedTopic editedTopic) throws SQLException {
		DbTransaction.performTransaction(editedTopicDao, () -> editedTopicDao.create(editedTopic));
	}

	private List<DiscussionItem> getDiscussionItems(String rootHandle, ContentType contentType)
		throws SQLException {

		QueryBuilder<DiscussionItem, Integer> builder = discussionItemDao.queryBuilder();
		builder.where()
			.eq(DbSchemas.DiscussionItem.ROOT_HANDLE, rootHandle)
			.and().eq(DbSchemas.DiscussionItem.CONTENT_TYPE, contentType);
		builder.orderBy(DbSchemas.DiscussionItem.ID, false);

		return builder.query();
	}

	/**
	 * Removes a previously saved post.
	 * @param post	the post to remove
	 */
	public void removePost(AddPostData post) {
		if (post.hasImage()) {
			deleteSafely(post.getImagePath());
		}
		try {
			postDao.delete(post);
		} catch (SQLException e) {
			DebugLog.logException(e);
		}
	}

	/**
	 * Removes a post by its id.
	 * @param   postId    id of the post
	 */
	public void removePostById(int postId) {
		try {
			AddPostData postData = postDao.queryForId(postId);
			if (postData != null) {
				removePost(postData);
			}
		} catch (SQLException e) {
			DebugLog.logException(e);
		}
	}

	/**
	 * Removes an unsent comment by its id.
	 * @param commentId id of the comment
	 */
	public void removeUnsentComment(int commentId) {
		DiscussionItem discussionItem = removeDiscussionItem(commentId);
		if (discussionItem != null) {
			try {
				DbTransaction.performTransaction(topicDao,
					() -> topicDao.executeRaw(
						ON_UNSENT_COMMENT_REMOVED_STATEMENT,
						discussionItem.getRootHandle()
					)
				);
			} catch (SQLException e) {
				DebugLog.logException(e);
			}
		}
	}

	private DiscussionItem removeDiscussionItem(int itemId) {
		DiscussionItem item;

		try {
			item = discussionItemDao.queryForId(itemId);
			DbTransaction.performTransaction(discussionItemDao, () -> discussionItemDao.deleteById(itemId));
		} catch (SQLException e) {
			DebugLog.logException(e);
			item = null;
		}

		return item;
	}

	/**
	 * Removes an unsent reply by its id.
	 * @param replyId   id of the reply
	 */
	public void removeUnsentReply(int replyId) {
		DiscussionItem item = removeDiscussionItem(replyId);
		if (item != null) {
			try {
				DbTransaction.performTransaction(commentDao,
					() -> commentDao.executeRaw(ON_UNSENT_REPLY_REMOVED_STATEMENT, item.getRootHandle()));
			} catch (SQLException e) {
				DebugLog.logException(e);
			}
		}
	}

	/**
	 * Gets pending posts in a form ready for displaying.
	 * @return  list of {@linkplain TopicView} instances.
	 */
	public List<TopicView> getPendingPostsForDisplay() {
		List<TopicView> result = new ArrayList<>();
		List<AddPostData> pendingPosts = getAllPendingPosts();

		for (AddPostData pendingPost : pendingPosts) {
			result.add(asTopicView(pendingPost));
		}

		return result;
	}

	private List<AddPostData> getAllPendingPosts() {
		List<AddPostData> result;

		try {
			result = postDao.queryForAll();
		} catch (SQLException e) {
			DebugLog.logException(e);
			result = Collections.emptyList();
		}

		return result;
	}

	private static TopicView asTopicView(AddPostData postData) {
		String imageUrl;

		if (postData.hasImage()) {
			imageUrl = Uri.fromFile(new File(postData.getImagePath())).toString();
		} else {
			imageUrl = null;
		}

		return new TopicView.Builder()
			.setPublisherType(PublisherType.USER.ordinal())
			.setTopicTitle(postData.getTitle())
			.setTopicText(postData.getDescription())
			.setCreatedTime(System.currentTimeMillis())
			.setLocal(true)
			.setLocalPostId(postData.getId())
			.setTopicHandle(TopicView.LOCAL_TOPIC_HANDLE_PREFIX + postData.hashCode())
			.setTopicBlobType(BlobType.IMAGE.ordinal())
			.setTopicBlobUrl(imageUrl)
			.setUser(UserAccount.getInstance().generateCompactUserView())
			.build();
	}
}
