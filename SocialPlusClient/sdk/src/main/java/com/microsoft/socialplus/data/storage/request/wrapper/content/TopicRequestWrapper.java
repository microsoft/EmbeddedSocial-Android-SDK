/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.data.storage.request.wrapper.content;

import com.microsoft.socialplus.data.storage.TopicCache;
import com.microsoft.socialplus.data.storage.request.wrapper.AbstractNetworkMethodWrapper;
import com.microsoft.socialplus.server.model.content.topics.GetTopicRequest;
import com.microsoft.socialplus.server.model.content.topics.GetTopicResponse;

import java.sql.SQLException;

public class TopicRequestWrapper extends AbstractNetworkMethodWrapper<GetTopicRequest, GetTopicResponse> {

	private final TopicCache topicCache;

	public TopicRequestWrapper(INetworkMethod<GetTopicRequest, GetTopicResponse> networkMethod,
							   TopicCache topicCache) {

		super(networkMethod);
		this.topicCache = topicCache;
	}

	@Override
	protected void storeResponse(GetTopicRequest request, GetTopicResponse response)
			throws SQLException {

		if (response.getTopic() == null) {
			return;
		}
		topicCache.storeTopic(response.getTopic());
	}

	@Override
	protected GetTopicResponse getCachedResponse(GetTopicRequest request) throws SQLException {
		GetTopicResponse response = topicCache.getSingleTopicResponse(request);

		if (response.getTopic() == null) {
			throw new SQLException("Couldn't find cached topic " + request.getTopicHandle());
		}

		return response;
	}
}