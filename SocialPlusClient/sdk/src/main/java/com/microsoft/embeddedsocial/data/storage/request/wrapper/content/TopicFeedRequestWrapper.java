/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage.request.wrapper.content;

import android.text.TextUtils;

import com.microsoft.embeddedsocial.account.UserAccount;
import com.microsoft.embeddedsocial.data.model.TopicFeedType;
import com.microsoft.embeddedsocial.data.storage.ContentCache;
import com.microsoft.embeddedsocial.data.storage.PostStorage;
import com.microsoft.embeddedsocial.data.storage.request.wrapper.AbstractBatchNetworkMethodWrapper;
import com.microsoft.embeddedsocial.server.model.content.topics.GetTopicFeedRequest;
import com.microsoft.embeddedsocial.server.model.content.topics.TopicsListResponse;

import java.sql.SQLException;

public class TopicFeedRequestWrapper extends AbstractBatchNetworkMethodWrapper<GetTopicFeedRequest, TopicsListResponse> {

	private final ContentCache contentCache;
	private final PostStorage postStorage;

	public TopicFeedRequestWrapper(INetworkMethod<GetTopicFeedRequest, TopicsListResponse> networkMethod,
	                               PostStorage postStorage, ContentCache contentCache) {

		super(networkMethod);
		this.postStorage = postStorage;
		this.contentCache = contentCache;
	}

	private void addPendingPosts(GetTopicFeedRequest request, TopicsListResponse topicFeed) {
		if (TextUtils.isEmpty(request.getCursor()) && shouldFeedContainPendingPosts(request)) {
			topicFeed.getData().addAll(0, postStorage.getPendingPostsForDisplay());
		}
	}

	private boolean shouldFeedContainPendingPosts(GetTopicFeedRequest request) {
		TopicFeedType feedType = request.getTopicFeedType();
		return feedType == TopicFeedType.FOLLOWING_RECENT
			|| (feedType == TopicFeedType.USER_RECENT && UserAccount.getInstance().isCurrentUser(request.getQuery()));
	}

	@Override
	protected void storeResponse(GetTopicFeedRequest request, TopicsListResponse response)
		throws SQLException {

		contentCache.storeFeed(request, response);
	}

	@Override
	protected TopicsListResponse getCachedResponse(GetTopicFeedRequest request) throws SQLException {
		return contentCache.getResponse(request);
	}

	@Override
	protected void onResponseIsReady(GetTopicFeedRequest request, TopicsListResponse response,
	                                 boolean cachedResponseUsed) {

		addPendingPosts(request, response);
	}
}
