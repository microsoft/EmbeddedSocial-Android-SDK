/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.data.storage.request.wrapper.content;

import com.microsoft.socialplus.data.storage.TopicCache;
import com.microsoft.socialplus.data.storage.request.wrapper.AbstractNetworkMethodWrapper;
import com.microsoft.socialplus.server.model.content.comments.GetCommentRequest;
import com.microsoft.socialplus.server.model.content.comments.GetCommentResponse;

import java.sql.SQLException;

public class CommentRequestWrapper extends AbstractNetworkMethodWrapper<GetCommentRequest, GetCommentResponse> {

	private final TopicCache topicCache;

	public CommentRequestWrapper(INetworkMethod<GetCommentRequest, GetCommentResponse> networkMethod,
	                             TopicCache topicCache) {

		super(networkMethod);
		this.topicCache = topicCache;
	}

	@Override
	protected void storeResponse(GetCommentRequest request, GetCommentResponse response)
		throws SQLException {

		topicCache.storeComment(response.getComment());
	}

	@Override
	protected GetCommentResponse getCachedResponse(GetCommentRequest request) throws SQLException {
		return new GetCommentResponse(topicCache.getComment(request.getCommentHandle()));
	}
}
