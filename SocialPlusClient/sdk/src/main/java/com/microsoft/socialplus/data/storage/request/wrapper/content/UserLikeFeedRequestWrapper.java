/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.data.storage.request.wrapper.content;

import com.microsoft.socialplus.data.storage.TopicCache;
import com.microsoft.socialplus.data.storage.request.wrapper.AbstractBatchNetworkMethodWrapper;
import com.microsoft.socialplus.server.model.FeedUserRequest;
import com.microsoft.socialplus.server.model.content.topics.TopicsListResponse;

import java.sql.SQLException;

public class UserLikeFeedRequestWrapper extends AbstractBatchNetworkMethodWrapper<FeedUserRequest, TopicsListResponse> {

	private final TopicCache topicCache;

	public UserLikeFeedRequestWrapper(INetworkMethod<FeedUserRequest, TopicsListResponse> networkMethod,
	                                  TopicCache topicCache) {

		super(networkMethod);
		this.topicCache = topicCache;
	}

	@Override
	protected void storeResponse(FeedUserRequest request, TopicsListResponse response)
		throws SQLException {

		topicCache.storeLikeFeed(request, response);
	}

	@Override
	protected TopicsListResponse getCachedResponse(FeedUserRequest request)
		throws SQLException {

		return topicCache.getLikeFeedResponse();
	}
}
