/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage.syncadapter;

import android.text.TextUtils;

import com.j256.ormlite.dao.Dao;
import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.data.model.CommentFeedType;
import com.microsoft.embeddedsocial.data.model.DiscussionItem;
import com.microsoft.embeddedsocial.data.storage.DatabaseHelper;
import com.microsoft.embeddedsocial.data.storage.exception.FatalDatabaseException;
import com.microsoft.embeddedsocial.data.storage.model.CommentFeedRelation;
import com.microsoft.embeddedsocial.data.storage.transaction.DbTransaction;
import com.microsoft.embeddedsocial.event.content.CommentPostedToBackendEvent;
import com.microsoft.embeddedsocial.event.content.ReplyPostedToBackendEvent;
import com.microsoft.embeddedsocial.server.IContentService;
import com.microsoft.embeddedsocial.server.ImageUploader;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.content.comments.AddCommentRequest;
import com.microsoft.embeddedsocial.server.model.content.comments.AddCommentResponse;
import com.microsoft.embeddedsocial.server.model.content.comments.GetCommentRequest;
import com.microsoft.embeddedsocial.server.model.content.comments.GetCommentResponse;
import com.microsoft.embeddedsocial.server.model.content.replies.AddReplyRequest;
import com.microsoft.embeddedsocial.server.model.content.replies.AddReplyResponse;
import com.microsoft.embeddedsocial.server.model.content.replies.GetReplyRequest;
import com.microsoft.embeddedsocial.server.model.content.replies.GetReplyResponse;
import com.microsoft.embeddedsocial.server.model.view.CommentView;
import com.microsoft.embeddedsocial.server.model.view.ReplyView;
import com.microsoft.embeddedsocial.server.sync.exception.SynchronizationException;
import com.microsoft.embeddedsocial.autorest.models.BlobType;
import com.microsoft.embeddedsocial.autorest.models.ContentType;
import com.microsoft.embeddedsocial.autorest.models.ImageType;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Sync adapter for comments and replies.
 */
public class DiscussionItemSyncAdapter extends AbstractAutoCleanupSyncAdapter<DiscussionItem> {

	private final Dao<CommentView, String> commentDao;
	private final Dao<ReplyView, String> replyDao;
	private final Dao<CommentFeedRelation, Integer> commentFeedDao;

	public DiscussionItemSyncAdapter(DiscussionItem item, Dao<DiscussionItem, ?> itemDao) {
		super(item, itemDao);
		DatabaseHelper databaseHelper = GlobalObjectRegistry.getObject(DatabaseHelper.class);
		this.commentDao = databaseHelper.getCommentDao();
		this.replyDao = databaseHelper.getReplyDao();
		try {
			this.commentFeedDao = GlobalObjectRegistry.getObject(DatabaseHelper.class)
				.getDao(CommentFeedRelation.class);
		} catch (SQLException e) {
			throw new FatalDatabaseException(e);
		}
	}

	@Override
	protected void onSynchronize(DiscussionItem item) throws NetworkRequestException,
			SynchronizationException {

		ContentType contentType = item.getContentType();
		switch (contentType) {
			case COMMENT:
				postComment(item);
				break;

			case REPLY:
				postReply(item);
				break;
		}
	}

	private void postReply(DiscussionItem item) throws NetworkRequestException {
		IContentService contentService = getServiceProvider().getContentService();
		AddReplyResponse response = contentService.addReply(
			new AddReplyRequest(item.getRootHandle(), item.getContentText()));

		new ReplyPostedToBackendEvent().submit();
		try {
			GetReplyRequest dataRequest = new GetReplyRequest(response.getReplyHandle());
			GetReplyResponse replyResponse = contentService.getReply(dataRequest);
			if (replyResponse.getReply() != null) {
				replyDao.createOrUpdate(replyResponse.getReply());
			} else {
				DebugLog.e("didn't receive the posted reply from the server");
			}
		} catch (SQLException | NetworkRequestException e) {
			DebugLog.logException(e);
		}
	}

	private void postComment(DiscussionItem item) throws NetworkRequestException {
		IContentService contentService = getServiceProvider().getContentService();

		try {
			String imagePath = item.getImagePath();
			String imageUrl = null;
			try {
				if (!TextUtils.isEmpty(imagePath)) {
					imageUrl = ImageUploader.uploadImage(new File(imagePath), ImageType.CONTENTBLOB);
				}
			} catch (IOException e) {
				DebugLog.logException(e);
			}
			AddCommentResponse response = contentService.addComment(
					new AddCommentRequest(item.getRootHandle(), item.getContentText(),
							BlobType.IMAGE, imageUrl));

			new CommentPostedToBackendEvent().submit();

			GetCommentRequest dataRequest = new GetCommentRequest(response.getCommentHandle());
			GetCommentResponse commentResponse = contentService.getComment(dataRequest);
			CommentView comment = commentResponse.getComment();
			if (comment != null) {
				commentDao.createOrUpdate(comment);
				CommentFeedRelation commentFeedRelation = new CommentFeedRelation(
					CommentFeedType.RECENT.ordinal(),
					comment.getTopicHandle(),
					comment.getHandle()
				);
				DbTransaction.performTransaction(commentFeedDao,
					() -> commentFeedDao.create(commentFeedRelation));
			} else {
				DebugLog.e("didn't receive the posted comment from the server");
			}
		} catch (SQLException | NetworkRequestException e) {
			DebugLog.logException(e);
		}
	}
}
