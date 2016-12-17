/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage.syncadapter;

import android.text.TextUtils;

import com.j256.ormlite.dao.Dao;
import com.microsoft.embeddedsocial.account.UserAccount;
import com.microsoft.embeddedsocial.data.storage.model.TopicFeedRelation;
import com.microsoft.embeddedsocial.event.sync.PostUploadFailedEvent;
import com.microsoft.embeddedsocial.event.sync.PostUploadedEvent;
import com.microsoft.embeddedsocial.server.ImageUploader;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.content.topics.AddTopicRequest;
import com.microsoft.embeddedsocial.server.model.content.topics.GetTopicResponse;
import com.microsoft.embeddedsocial.server.sync.exception.SynchronizationException;
import com.microsoft.embeddedsocial.autorest.models.BlobType;
import com.microsoft.embeddedsocial.autorest.models.ImageType;
import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.data.model.AddPostData;
import com.microsoft.embeddedsocial.data.model.TopicFeedType;
import com.microsoft.embeddedsocial.data.storage.DatabaseHelper;
import com.microsoft.embeddedsocial.data.storage.PostStorage;
import com.microsoft.embeddedsocial.data.storage.exception.FatalDatabaseException;
import com.microsoft.embeddedsocial.data.storage.transaction.DbTransaction;
import com.microsoft.embeddedsocial.server.IContentService;
import com.microsoft.embeddedsocial.server.EmbeddedSocialServiceProvider;
import com.microsoft.embeddedsocial.server.exception.BadRequestException;
import com.microsoft.embeddedsocial.server.model.content.topics.AddTopicResponse;
import com.microsoft.embeddedsocial.server.model.content.topics.GetTopicRequest;
import com.microsoft.embeddedsocial.server.model.view.TopicView;
import com.microsoft.embeddedsocial.server.sync.ISynchronizable;
import com.microsoft.embeddedsocial.server.sync.exception.OperationRejectedException;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Sync adapter for posts.
 */
public class PostSyncAdapter implements ISynchronizable {

	private final PostStorage storage;
	private final AddPostData postData;
	private final Dao<TopicFeedRelation, Integer> topicFeedDao;

	public PostSyncAdapter(PostStorage storage, AddPostData postData) {
		this.storage = storage;
		this.postData = postData;
		try {
			this.topicFeedDao = GlobalObjectRegistry.getObject(DatabaseHelper.class)
				.getDao(TopicFeedRelation.class);
		} catch (SQLException e) {
			throw new FatalDatabaseException(e);
		}
	}

	@Override
	public void synchronize() throws SynchronizationException {
		IContentService contentService = GlobalObjectRegistry
			.getObject(EmbeddedSocialServiceProvider.class)
			.getContentService();

		AddTopicRequest.Builder requestBuilder = new AddTopicRequest.Builder()
			.setTopicTitle(postData.getTitle())
			.setTopicText(postData.getDescription())
			.setPublisherType(postData.getPublisherType());

		try {
			String imagePath = postData.getImagePath();
			if (!TextUtils.isEmpty(imagePath)) {
				String imageUrl = ImageUploader.uploadImage(new File(imagePath), ImageType.CONTENTBLOB);
				requestBuilder.setTopicBlobType(BlobType.IMAGE);
				requestBuilder.setTopicBlobHandle(imageUrl);
			} else {
				imagePath = null;
			}
			AddTopicResponse response = contentService.addTopic(requestBuilder.build());
			loadTopicToCache(contentService, response.getTopicHandle(), imagePath);
		} catch (BadRequestException e) {
			throw new OperationRejectedException(e);
		} catch (IOException | NetworkRequestException e) {
			DebugLog.logException(e);
			new PostUploadFailedEvent().submit();
			throw new SynchronizationException("Post sync failed: " + e.getMessage(), e);
		}
	}

	private void loadTopicToCache(IContentService contentService, String topicHandle, String imagePath) {
		try {
			// by requesting the topic it is stored to cache automatically
			boolean loadFromCache = false;
			GetTopicResponse response = contentService.getTopic(new GetTopicRequest(topicHandle, imagePath));
			TopicView topic = response.getTopic();
			if (topic != null) {
				TopicFeedRelation userRecentRelation = new TopicFeedRelation(
					UserAccount.getInstance().getUserHandle(),
					TopicFeedType.USER_RECENT.ordinal(),
					topicHandle
				);
				TopicFeedRelation homeFeedRelation = new TopicFeedRelation(
					"",
					TopicFeedType.FOLLOWING_RECENT.ordinal(),
					topicHandle
				);
				DbTransaction.performTransaction(topicFeedDao, () -> {
					topicFeedDao.create(homeFeedRelation);
					topicFeedDao.create(userRecentRelation);
				});
			}
		} catch (NetworkRequestException | SQLException e) {
			DebugLog.logException(e);
			// can't rethrow this exception here, cause this operation is optional
			// and should not lead to sync failure
		}
	}

	@Override
	public void onSynchronizationSuccess() {
		storage.removePost(postData);
		new PostUploadedEvent().submit();
	}
}
