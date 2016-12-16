/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage.request.wrapper.content;

import com.microsoft.embeddedsocial.server.model.content.topics.GetTopicResponse;
import com.microsoft.embeddedsocial.data.storage.ContentCache;
import com.microsoft.embeddedsocial.data.storage.request.wrapper.AbstractNetworkMethodWrapper;
import com.microsoft.embeddedsocial.server.model.content.topics.GetTopicRequest;

import java.sql.SQLException;

public class TopicRequestWrapper extends AbstractNetworkMethodWrapper<GetTopicRequest, GetTopicResponse> {

	private final ContentCache contentCache;

	public TopicRequestWrapper(INetworkMethod<GetTopicRequest, GetTopicResponse> networkMethod,
							   ContentCache contentCache) {

		super(networkMethod);
		this.contentCache = contentCache;
	}

	@Override
	protected void storeResponse(GetTopicRequest request, GetTopicResponse response)
			throws SQLException {

		if (response.getTopic() == null) {
			return;
		}
		contentCache.storeTopic(response.getTopic());
	}

	@Override
	protected GetTopicResponse getCachedResponse(GetTopicRequest request) throws SQLException {
		GetTopicResponse response = contentCache.getSingleTopicResponse(request);

		if (response.getTopic() == null) {
			throw new SQLException("Couldn't find cached topic " + request.getTopicHandle());
		}

		return response;
	}
}