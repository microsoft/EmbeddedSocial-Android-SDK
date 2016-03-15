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
import com.microsoft.socialplus.server.model.pin.GetPinFeedRequest;

import java.sql.SQLException;

public class PinFeedRequestWrapper extends AbstractBatchNetworkMethodWrapper<GetPinFeedRequest, TopicsListResponse> {

	private final TopicCache topicCache;

	public PinFeedRequestWrapper(INetworkMethod<GetPinFeedRequest, TopicsListResponse> networkMethod,
	                             TopicCache topicCache) {

		super(networkMethod);
		this.topicCache = topicCache;
	}

	@Override
	protected void storeResponse(GetPinFeedRequest request, TopicsListResponse response) throws SQLException {
		topicCache.storePinFeed(request, response);
	}

	@Override
	protected TopicsListResponse getCachedResponse(GetPinFeedRequest request) throws SQLException {
		return topicCache.getPinFeedResponse();
	}
}
