/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.data.storage.request.wrapper.content;

import com.microsoft.socialplus.data.storage.TopicCache;
import com.microsoft.socialplus.data.storage.request.wrapper.AbstractNetworkMethodWrapper;
import com.microsoft.socialplus.server.model.content.replies.GetReplyRequest;
import com.microsoft.socialplus.server.model.content.replies.GetReplyResponse;

import java.sql.SQLException;

public class ReplyRequestWrapper extends AbstractNetworkMethodWrapper<GetReplyRequest, GetReplyResponse> {

	private final TopicCache topicCache;

	public ReplyRequestWrapper(INetworkMethod<GetReplyRequest, GetReplyResponse> networkMethod,
	                           TopicCache topicCache) {

		super(networkMethod);
		this.topicCache = topicCache;
	}

	@Override
	protected void storeResponse(GetReplyRequest request, GetReplyResponse response)
		throws SQLException {

		topicCache.storeReply(response.getReply());
	}

	@Override
	protected GetReplyResponse getCachedResponse(GetReplyRequest request) throws SQLException {
		return new GetReplyResponse(topicCache.getReply(request.getReplyHandle()));
	}
}
