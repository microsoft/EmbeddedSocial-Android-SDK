/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.data.storage.request.wrapper.content;

import android.text.TextUtils;

import com.microsoft.socialplus.account.UserAccount;
import com.microsoft.socialplus.data.model.TopicFeedType;
import com.microsoft.socialplus.data.storage.PostStorage;
import com.microsoft.socialplus.data.storage.TopicCache;
import com.microsoft.socialplus.data.storage.request.wrapper.AbstractBatchNetworkMethodWrapper;
import com.microsoft.socialplus.server.model.content.topics.GetTopicFeedRequest;
import com.microsoft.socialplus.server.model.content.topics.TopicsListResponse;

import java.sql.SQLException;

public class TopicFeedRequestWrapper extends AbstractBatchNetworkMethodWrapper<GetTopicFeedRequest, TopicsListResponse> {

	private final TopicCache topicCache;
	private final PostStorage postStorage;

	public TopicFeedRequestWrapper(INetworkMethod<GetTopicFeedRequest, TopicsListResponse> networkMethod,
	                               PostStorage postStorage, TopicCache topicCache) {

		super(networkMethod);
		this.postStorage = postStorage;
		this.topicCache = topicCache;
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

		topicCache.storeFeed(request, response);
	}

	@Override
	protected TopicsListResponse getCachedResponse(GetTopicFeedRequest request) throws SQLException {
		return topicCache.getResponse(request);
	}

	@Override
	protected void onResponseIsReady(GetTopicFeedRequest request, TopicsListResponse response,
	                                 boolean cachedResponseUsed) {

		addPendingPosts(request, response);
	}
}
