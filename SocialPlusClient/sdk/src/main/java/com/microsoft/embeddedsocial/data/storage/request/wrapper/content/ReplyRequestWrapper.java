/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage.request.wrapper.content;

import com.microsoft.embeddedsocial.data.storage.ContentCache;
import com.microsoft.embeddedsocial.data.storage.request.wrapper.AbstractNetworkMethodWrapper;
import com.microsoft.embeddedsocial.server.model.content.replies.GetReplyRequest;
import com.microsoft.embeddedsocial.server.model.content.replies.GetReplyResponse;

import java.sql.SQLException;

public class ReplyRequestWrapper extends AbstractNetworkMethodWrapper<GetReplyRequest, GetReplyResponse> {

	private final ContentCache contentCache;

	public ReplyRequestWrapper(INetworkMethod<GetReplyRequest, GetReplyResponse> networkMethod,
							   ContentCache contentCache) {

		super(networkMethod);
		this.contentCache = contentCache;
	}

	@Override
	protected void storeResponse(GetReplyRequest request, GetReplyResponse response)
		throws SQLException {

		contentCache.storeReply(response.getReply());
	}

	@Override
	protected GetReplyResponse getCachedResponse(GetReplyRequest request) throws SQLException {
		return new GetReplyResponse(contentCache.getReply(request.getReplyHandle()));
	}
}
