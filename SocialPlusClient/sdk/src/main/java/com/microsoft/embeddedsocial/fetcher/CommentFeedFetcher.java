/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.fetcher;

import android.support.annotation.NonNull;

import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.data.model.AccountData;
import com.microsoft.embeddedsocial.data.model.CommentFeedType;
import com.microsoft.embeddedsocial.fetcher.base.DataState;
import com.microsoft.embeddedsocial.fetcher.base.Fetcher;
import com.microsoft.embeddedsocial.fetcher.base.PartiallyLoadedDataException;
import com.microsoft.embeddedsocial.fetcher.base.RequestType;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.account.GetUserProfileRequest;
import com.microsoft.embeddedsocial.server.model.content.comments.GetCommentFeedRequest;
import com.microsoft.embeddedsocial.server.model.content.topics.GetTopicResponse;
import com.microsoft.embeddedsocial.server.model.view.CommentView;
import com.microsoft.embeddedsocial.server.model.view.TopicView;
import com.microsoft.embeddedsocial.autorest.models.PublisherType;
import com.microsoft.embeddedsocial.server.IAccountService;
import com.microsoft.embeddedsocial.server.IContentService;
import com.microsoft.embeddedsocial.server.EmbeddedSocialServiceProvider;
import com.microsoft.embeddedsocial.server.model.content.topics.GetTopicRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Fetches data for the topic with comments page.
 */
//TODO remove access modifier
public class CommentFeedFetcher extends Fetcher<Object> {
	private static final String TOPIC_LOADED = "topicLoaded";

	protected final IContentService contentService;
	protected String topicHandle;
	protected DataRequestExecutor<CommentView, ?> commentFeedRequestExecutor;
	private TopicView topicView;

	protected CommentFeedFetcher() {
		this.contentService = GlobalObjectRegistry.getObject(EmbeddedSocialServiceProvider.class).getContentService();
	}

	public CommentFeedFetcher(CommentFeedType feedType, String topicHandle, TopicView topicView) {
		this();
		this.topicHandle = topicHandle;
		this.topicView = topicView;

		commentFeedRequestExecutor = new BatchDataRequestExecutor<>(
			contentService::getCommentFeed,
			() -> new GetCommentFeedRequest(feedType, topicHandle)
		);
	}

	@Override
	protected List<Object> fetchDataPage(DataState dataState, RequestType requestType, int pageSize) throws Exception {
		List<Object> result = new ArrayList<>();
		boolean readTopic = !dataState.getBooleanValue(TOPIC_LOADED);
		if (readTopic) {
			result.add(getTopic(requestType));
		}
		try {
			List<CommentView> topics = commentFeedRequestExecutor.fetchData(dataState, requestType, pageSize);
			result.addAll(topics);
			return result;
		} catch (NetworkRequestException e) {
			throw readTopic ? new PartiallyLoadedDataException(result, e) : e;
		} finally {
			dataState.setValue(TOPIC_LOADED, true);
		}
	}

	@NonNull
	private TopicView getTopic(RequestType requestType) throws Exception {
		TopicView result = topicView == null || requestType.isFullDataReloadRequired() ? readTopic(requestType) : topicView;
		IAccountService accountService = GlobalObjectRegistry.getObject(EmbeddedSocialServiceProvider.class).getAccountService();
		if (result.getPublisherType() == PublisherType.USER) {
			GetUserProfileRequest request = new GetUserProfileRequest(result.getUser().getHandle());
			if (requestType == RequestType.SYNC_WITH_CACHE) {
				request.forceCacheUsage();
			}
			AccountData profile = new AccountData(accountService.getUserProfile(request).getUser());
			result.setUserProfile(profile);
		}
		return result;
	}

	protected TopicView readTopic(RequestType requestType) throws Exception {
		GetTopicRequest request = new GetTopicRequest(topicHandle);
		if (requestType == RequestType.SYNC_WITH_CACHE) {
			request.forceCacheUsage();
		}
		GetTopicResponse response = contentService.getTopic(request);
		if (response.getTopic() == null) {
			throw new EmptyDataException("topic is null");
		}
		return response.getTopic();
	}
}
