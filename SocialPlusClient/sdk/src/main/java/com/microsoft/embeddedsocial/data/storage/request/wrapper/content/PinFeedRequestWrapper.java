/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage.request.wrapper.content;

import com.microsoft.embeddedsocial.server.model.content.topics.TopicsListResponse;
import com.microsoft.embeddedsocial.server.model.pin.GetPinFeedRequest;
import com.microsoft.embeddedsocial.data.storage.ContentCache;
import com.microsoft.embeddedsocial.data.storage.request.wrapper.AbstractBatchNetworkMethodWrapper;

import java.sql.SQLException;

public class PinFeedRequestWrapper extends AbstractBatchNetworkMethodWrapper<GetPinFeedRequest, TopicsListResponse> {

	private final ContentCache contentCache;

	public PinFeedRequestWrapper(INetworkMethod<GetPinFeedRequest, TopicsListResponse> networkMethod,
								 ContentCache contentCache) {

		super(networkMethod);
		this.contentCache = contentCache;
	}

	@Override
	protected void storeResponse(GetPinFeedRequest request, TopicsListResponse response) throws SQLException {
		contentCache.storePinFeed(request, response);
	}

	@Override
	protected TopicsListResponse getCachedResponse(GetPinFeedRequest request) throws SQLException {
		return contentCache.getPinFeedResponse();
	}
}
