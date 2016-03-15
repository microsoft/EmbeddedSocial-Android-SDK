/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.content.replies;

import com.microsoft.autorest.models.FeedResponseReplyView;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.socialplus.server.model.FeedUserRequest;

import java.io.IOException;

public class GetReplyFeedRequest extends FeedUserRequest {

	private final String commentHandle;

	public GetReplyFeedRequest(String commentHandle) {
		this.commentHandle = commentHandle;
	}

	public String getCommentHandle() {
		return commentHandle;
	}

	@Override
	public GetReplyFeedResponse send() throws ServiceException, IOException {
		ServiceResponse<FeedResponseReplyView> serviceResponse =
				COMMENT_REPLIES.getReplies(commentHandle, bearerToken, getCursor(), getBatchSize());
		return new GetReplyFeedResponse(serviceResponse.getBody());
	}
}
