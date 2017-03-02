/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage.request.wrapper.content;

import com.microsoft.embeddedsocial.data.storage.ContentCache;
import com.microsoft.embeddedsocial.data.storage.request.wrapper.AbstractNetworkMethodWrapper;
import com.microsoft.embeddedsocial.server.model.content.comments.GetCommentRequest;
import com.microsoft.embeddedsocial.server.model.content.comments.GetCommentResponse;

import java.sql.SQLException;

public class CommentRequestWrapper extends AbstractNetworkMethodWrapper<GetCommentRequest, GetCommentResponse> {

	private final ContentCache contentCache;

	public CommentRequestWrapper(INetworkMethod<GetCommentRequest, GetCommentResponse> networkMethod,
								 ContentCache contentCache) {

		super(networkMethod);
		this.contentCache = contentCache;
	}

	@Override
	protected void storeResponse(GetCommentRequest request, GetCommentResponse response)
		throws SQLException {

		contentCache.storeComment(response.getComment());
	}

	@Override
	protected GetCommentResponse getCachedResponse(GetCommentRequest request) throws SQLException {
		return new GetCommentResponse(contentCache.getComment(request.getCommentHandle()));
	}
}
