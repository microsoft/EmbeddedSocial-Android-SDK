/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage;

import android.text.TextUtils;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.base.utils.debug.DebugTimer;
import com.microsoft.embeddedsocial.data.model.TopicFeedType;
import com.microsoft.embeddedsocial.data.storage.model.CommentFeedRelation;
import com.microsoft.embeddedsocial.data.storage.model.TopicFeedRelation;
import com.microsoft.embeddedsocial.data.storage.transaction.DbTransaction;
import com.microsoft.embeddedsocial.server.model.FeedUserRequest;
import com.microsoft.embeddedsocial.server.model.content.comments.GetCommentFeedRequest;
import com.microsoft.embeddedsocial.server.model.content.comments.GetCommentFeedResponse;
import com.microsoft.embeddedsocial.server.model.content.replies.GetReplyFeedResponse;
import com.microsoft.embeddedsocial.server.model.content.topics.GetTopicResponse;
import com.microsoft.embeddedsocial.server.model.content.topics.TopicsListResponse;
import com.microsoft.embeddedsocial.server.model.view.AppCompactView;
import com.microsoft.embeddedsocial.server.model.view.CommentView;
import com.microsoft.embeddedsocial.server.model.view.TopicView;
import com.microsoft.embeddedsocial.server.model.view.UserCompactView;
import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.data.storage.exception.FatalDatabaseException;
import com.microsoft.embeddedsocial.data.storage.model.EditedTopic;
import com.microsoft.embeddedsocial.server.model.TimedItem;
import com.microsoft.embeddedsocial.server.model.content.replies.GetReplyFeedRequest;
import com.microsoft.embeddedsocial.server.model.content.topics.GenericTopicRequest;
import com.microsoft.embeddedsocial.server.model.content.topics.GetTopicFeedRequest;
import com.microsoft.embeddedsocial.server.model.view.ReplyView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Provides cache for topics.
 */
public class ContentCache {

	private static final int LIKED_FEED_TYPE = -1001;
	private static final int PINNED_FEED_TYPE = -1002;

	private final Dao<TopicView, String> topicDao;
	private final Dao<CommentView, String> commentDao;
	private final Dao<ReplyView, String> replyDao;
	private final Dao<AppCompactView, String> appDao;
	private Dao<UserCompactView, String> userDao;
	private Dao<TopicFeedRelation, Integer> feedDao;
	private Dao<CommentFeedRelation, Integer> commentFeedDao;
	private Dao<EditedTopic, Integer> editedTopicDao;

	/**
	 * Default constructor.
	 */
	ContentCache() {
		DatabaseHelper helper = GlobalObjectRegistry.getObject(DatabaseHelper.class);
		topicDao = helper.getTopicDao();
		commentDao = helper.getCommentDao();
		replyDao = helper.getReplyDao();
		userDao = helper.getUserDao();
		appDao = helper.getAppDao();
		try {
			feedDao = helper.getDao(TopicFeedRelation.class);
			commentFeedDao = helper.getDao(CommentFeedRelation.class);
			editedTopicDao = helper.getDao(EditedTopic.class);
		} catch (SQLException e) {
			DebugLog.logException(e);
			throw new FatalDatabaseException(e);
		}
	}

	/**
	 * Gets cached response to a topic feed request.
	 * @param   request   topic feed request
	 * @return  cached topic feed response.
	 * @throws SQLException if the database fails
	 */
	public TopicsListResponse getResponse(GetTopicFeedRequest request) throws SQLException {
		TopicFeedType feedType = request.getTopicFeedType();
		boolean sortResults = feedType == TopicFeedType.USER_RECENT
				|| feedType == TopicFeedType.FOLLOWING_RECENT
				|| feedType == TopicFeedType.EVERYONE_RECENT
				|| feedType == TopicFeedType.MY_RECENT;

		return getResponse(feedType.ordinal(), request.getQuery(), sortResults);
	}

	/**
	 * Gets cached like feed response.
	 * @return  like feed response.
	 * @throws SQLException if the database fails.
	 */
	public TopicsListResponse getLikeFeedResponse() throws SQLException {
		return getResponse(LIKED_FEED_TYPE, TopicFeedRelation.DEFAULT_QUERY, false);
	}

	/**
	 * Gets cached pin feed response.
	 * @return  pin feed response.
	 * @throws SQLException if the database fails.
	 */
	public TopicsListResponse getPinFeedResponse() throws SQLException {
		return getResponse(PINNED_FEED_TYPE, TopicFeedRelation.DEFAULT_QUERY, false);
	}

	private TopicsListResponse getResponse(int feedType, String query, boolean sortResults)
		throws SQLException {

		String userHandleQuery = query != null ? query : TopicFeedRelation.DEFAULT_QUERY;
		PreparedQuery<TopicFeedRelation> feedQuery = feedDao.queryBuilder()
			.where()
			.eq(DbSchemas.TopicFeedRelation.FEED_TYPE, feedType)
			.and().eq(DbSchemas.TopicFeedRelation.QUERY, userHandleQuery)
			.prepare();

		List<TopicFeedRelation> relations = feedDao.query(feedQuery);
		List<TopicView> result = new ArrayList<>();
		for (TopicFeedRelation relation : relations) {
			TopicView topicView = topicDao.queryForId(relation.getTopicHandle());
			topicDao.refresh(topicView);
			result.add(topicView);
		}

		if (sortResults) {
			Collections.sort(result, TIMED_ITEM_COMPARATOR);
		}

		DebugLog.i(result.size() + " topics read from cache for feed type " + feedType
			+ " & query " + query);

		return new TopicsListResponse(result);
	}

	/**
	 * Gets cached response to a single topic request.
	 * @param request   the request
	 * @return  corresponding response.
	 * @throws SQLException if the database fails
	 */
	public GetTopicResponse getSingleTopicResponse(GenericTopicRequest request) throws SQLException {
		TopicView topic = topicDao.queryForId(request.getTopicHandle());
		if (topic != null) {
			topicDao.refresh(topic);
		}

		return new GetTopicResponse(topic);
	}

	/**
	 * Autorest version
	 * Gets cached response to a single topic request.
	 * @param topicHandle the request handle
	 * @return  corresponding response.
	 * @throws SQLException if the database fails
	 */
	public GetTopicResponse getSingleTopicResponse(String topicHandle) throws SQLException {
		TopicView topic = topicDao.queryForId(topicHandle);
		if (topic != null) {
			topicDao.refresh(topic);
		}

		return new GetTopicResponse(topic);
	}

	/**
	 * Stores a comment.
	 * @param comment           the comment to store
	 * @throws SQLException     if the database fails
	 */
	public void storeComment(CommentView comment) throws SQLException {
		DbTransaction.performTransaction(commentDao, () -> commentDao.createOrUpdate(comment));
	}

	/**
	 * Gets a comment by its handle
	 * @param commentHandle comment handle
	 * @return  comment instance.
	 * @throws SQLException if the database fails or the comment with the specified handle doesn't
	 * exist
	 */
	public CommentView getComment(String commentHandle) throws SQLException {
		CommentView comment = commentDao.queryForId(commentHandle);

		if (comment == null) {
			throw new SQLException("comment " + commentHandle + " not found in cache");
		}

		return comment;
	}

	/**
	 * Gets cached response for a comment feed request.
	 * @param request   the request
	 * @return  cached response.
	 * @throws SQLException if the database fails
	 */
	public GetCommentFeedResponse getCommentFeedResponse(GetCommentFeedRequest request)
		throws SQLException {

		List<CommentFeedRelation> relations = commentFeedDao.queryBuilder()
			.where()
			.eq(DbSchemas.CommentFeedRelation.FEED_TYPE, request.getCommentFeedType())
			.and()
			.eq(DbSchemas.CommentFeedRelation.TOPIC_HANDLE, request.getTopicHandle())
			.query();

		List<CommentView> comments = new ArrayList<>();
		for (CommentFeedRelation relation : relations) {
			CommentView comment = commentDao.queryForId(relation.getCommentHandle());
			commentDao.refresh(comment);
			comments.add(comment);
		}
		Collections.sort(comments, TIMED_ITEM_COMPARATOR);

		DebugLog.i(comments.size() + " comments read from cache for topic "
			+ request.getTopicHandle());

		return new GetCommentFeedResponse(comments);
	}

	/**
	 * Gets cached response for a reply feed request.
	 * @param request   the request
	 * @return  reply feed response.
	 * @throws SQLException if the database fails
	 */
	public GetReplyFeedResponse getReplyFeedResponse(GetReplyFeedRequest request) throws SQLException {
		List<ReplyView> results = replyDao.queryBuilder()
			.where()
			.eq(DbSchemas.Replies.COMMENT_HANDLE, request.getCommentHandle())
			.query();

		return new GetReplyFeedResponse(results);
	}

	/**
	 * Stores topic feed received from the server.
	 * @param request       the request
	 * @param response      server response to the request
	 * @throws SQLException if the database fails
	 */
	public void storeFeed(GetTopicFeedRequest request, TopicsListResponse response) throws SQLException {
		storeFeed(request, response, request.getTopicFeedType().ordinal(), request.getQuery());
	}

	/**
	 * Stores like feed received from the server.
	 * @param request       the request
	 * @param response      server response to the request
	 * @throws SQLException if the database fails
	 */
	public void storeLikeFeed(FeedUserRequest request, TopicsListResponse response) throws SQLException {
		storeFeed(request, response, LIKED_FEED_TYPE, TopicFeedRelation.DEFAULT_QUERY);
	}

	/**
	 * Stores pin feed received from the server.
	 * @param request       the request
	 * @param response      server response to the request
	 * @throws SQLException if the database fails
	 */
	public void storePinFeed(FeedUserRequest request, TopicsListResponse response) throws SQLException {
		storeFeed(request, response, PINNED_FEED_TYPE, TopicFeedRelation.DEFAULT_QUERY);
	}

	private void storeFeed(FeedUserRequest request, TopicsListResponse response,
		int feedType, String query) throws SQLException {

		DebugTimer.startInterval("saving topic feed");
		if (TextUtils.isEmpty(request.getCursor())) {
			DeleteBuilder<TopicFeedRelation, Integer> deleteBuilder = feedDao.deleteBuilder();
			String userHandleQuery = (query != null) ? query : TopicFeedRelation.DEFAULT_QUERY;
			deleteBuilder.where()
				.eq(DbSchemas.TopicFeedRelation.FEED_TYPE, feedType)
				.and().eq(DbSchemas.TopicFeedRelation.QUERY, userHandleQuery);
			deleteBuilder.delete();
		}
		DbTransaction.performTransaction(
			topicDao,
			() -> insertTopicFeedContents(response.getData(), feedType, query)
		);
		DebugTimer.endInterval();
		DebugLog.i("stored " + response.getData().size() + " topics for " + request);
	}

	private void insertTopicFeedContents(List<TopicView> feed, int feedType, String query)
		throws SQLException {

		updateTopicsEditedLocally(feed);

		for (TopicView topicView : feed) {
			storeTopicInternal(topicView);
			TopicFeedRelation relation = new TopicFeedRelation(
				query,
				feedType,
				topicView.getHandle()
			);
			feedDao.createOrUpdate(relation);
		}

	}

	/**
	 * Stores a comment feed.
	 * @param request       the request
	 * @param response      server response to the request
	 * @throws SQLException if the database fails.
	 */
	public void storeCommentFeed(GetCommentFeedRequest request, GetCommentFeedResponse response) throws SQLException {
		if (TextUtils.isEmpty(request.getCursor())) {
			DeleteBuilder<CommentFeedRelation, Integer> deleteBuilder = commentFeedDao.deleteBuilder();
			deleteBuilder.where()
				.eq(DbSchemas.CommentFeedRelation.TOPIC_HANDLE, request.getTopicHandle())
				.and()
				.eq(DbSchemas.CommentFeedRelation.FEED_TYPE, request.getCommentFeedType());
			deleteBuilder.delete();
		}
		DbTransaction.performTransaction(commentDao,
			() -> insertCommentFeedContent(request, response));
	}

	private void insertCommentFeedContent(GetCommentFeedRequest request,
		GetCommentFeedResponse response) throws SQLException {

		for (CommentView comment : response.getData()) {
			userDao.createOrUpdate(comment.getUser());
			commentDao.createOrUpdate(comment);
			CommentFeedRelation relation = new CommentFeedRelation(
					request.getCommentFeedType(),
					request.getTopicHandle(),
					comment.getHandle()
			);
			commentFeedDao.create(relation);
		}
	}

	/**
	 * Stores a reply feed.
	 * @param request   the request
	 * @param response  server response to the request
	 * @throws SQLException if the database fails
	 */
	public void storeReplyFeed(GetReplyFeedRequest request, GetReplyFeedResponse response) throws SQLException {
		if (TextUtils.isEmpty(request.getCursor())) {
			DeleteBuilder<ReplyView, String> deleteBuilder = replyDao.deleteBuilder();
			deleteBuilder.where().eq(DbSchemas.Replies.COMMENT_HANDLE, request.getCommentHandle());
			deleteBuilder.delete();
		}
		DbTransaction.performTransaction(replyDao, () -> {
			for (ReplyView reply : response.getData()) {
				userDao.createOrUpdate(reply.getUser());
				replyDao.createOrUpdate(reply);
			}
		});
	}

	/**
	 * Stores a topic.
	 * @param topicView the topic to store
	 * @throws SQLException if the database fails
	 */
	public void storeTopic(TopicView topicView) throws SQLException {
		updateTopicsEditedLocally(Collections.singletonList(topicView));
		storeTopicInternal(topicView);
	}

	private void updateTopicsEditedLocally(List<TopicView> topics) throws SQLException {
		for (TopicView topic : topics) {
			EditedTopic editedTopic = findEditedTopicByHandle(topic.getHandle());
			if (editedTopic != null) {
				topic.setTopicText(editedTopic.getTopicText());
				topic.setTopicTitle(editedTopic.getTopicTitle());
			}
		}
	}

	private EditedTopic findEditedTopicByHandle(String topicHandle) throws SQLException {
		QueryBuilder<EditedTopic, Integer> queryBuilder = editedTopicDao.queryBuilder();
		queryBuilder.where().eq(DbSchemas.Topics.TOPIC_HANDLE, topicHandle);
		return queryBuilder.queryForFirst();
	}

	private void storeTopicInternal(TopicView topicView) throws SQLException {
		userDao.createOrUpdate(topicView.getUser());
		appDao.createOrUpdate(topicView.getApp());
		topicDao.createOrUpdate(topicView);
	}

	/**
	 * Removes a topic.
	 * @param topicHandle   topic handle
	 */
	public void removeTopic(String topicHandle) {
		DeleteBuilder<TopicFeedRelation, Integer> deleteBuilder = feedDao.deleteBuilder();
		try {
			deleteBuilder.where().eq(DbSchemas.TopicFeedRelation.TOPIC_HANDLE, topicHandle);
			deleteBuilder.delete();
		} catch (SQLException e) {
			DebugLog.logException(e);
		}
	}

	/**
	 * Removes a comment.
	 * @param commentHandle comment handle
	 */
	public void removeComment(String commentHandle) {
		DeleteBuilder<CommentFeedRelation, Integer> deleteBuilder = commentFeedDao.deleteBuilder();
		try {
			String topicHandle = getCommentParentHandle(commentHandle);
			deleteBuilder.where().eq(DbSchemas.CommentFeedRelation.COMMENT_HANDLE, commentHandle);
			deleteBuilder.delete();
			if (!TextUtils.isEmpty(topicHandle)) {
				UpdateBuilder<TopicView, String> updateBuilder = topicDao.updateBuilder();
				updateBuilder.where()
					.eq(DbSchemas.Topics.TOPIC_HANDLE, topicHandle)
					.and().gt(DbSchemas.Topics.TOTAL_COMMENTS, 0);
				updateBuilder.updateColumnExpression(
					DbSchemas.Topics.TOTAL_COMMENTS,
					DbSchemas.Topics.TOTAL_COMMENTS + " - 1");
				updateBuilder.update();
			}
		} catch (SQLException e) {
			DebugLog.logException(e);
		}
	}

	private String getCommentParentHandle(String commentHandle) throws SQLException {
		CommentView comment = commentDao.queryForId(commentHandle);
		return comment != null ? comment.getTopicHandle() : "";
	}

	/**
	 * Removes a reply.
	 * @param replyHandle   reply handle
	 */
	public void removeReply(String replyHandle) throws SQLException {
		ReplyView reply = replyDao.queryForId(replyHandle);
		if (reply != null) {
			String commentHandle = reply.getCommentHandle();
			CommentView comment = commentDao.queryForId(commentHandle);
			if (comment != null) {
				commentDao.refresh(comment);
				long replies = comment.getTotalReplies();
				if (replies > 0) {
					comment.setTotalReplies(replies - 1);
					commentDao.update(comment);
				}
			}
			replyDao.delete(reply);
		}
	}

	/**
	 * Stores a reply.
	 * @param reply the reply to store
	 * @throws SQLException if the database fails
	 */
	public void storeReply(ReplyView reply) throws SQLException {
		DbTransaction.performTransaction(replyDao, () -> replyDao.createOrUpdate(reply));
	}

	/**
	 * Gets a reply by its handle.
	 * @param replyHandle   reply handle
	 * @return  reply with the specified handle
	 * @throws SQLException if database fails or the reply doesn't exist
	 */
	public ReplyView getReply(String replyHandle) throws SQLException {
		ReplyView reply = replyDao.queryForId(replyHandle);

		if (reply == null) {
			throw new SQLException("reply " + replyHandle + " was not found in cache");
		}

		return reply;
	}

	/**
	 * A comparator for timed items.
	 */
	private static final Comparator<? super TimedItem> TIMED_ITEM_COMPARATOR = (left, right) -> {
		long leftTime = left.getElapsedSeconds();
		long rightTime = right.getElapsedSeconds();

		if (leftTime == rightTime) {
			return 0;
		} else if (leftTime > rightTime) {
			return 1;
		} else {
			return -1;
		}
	};
}
