/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.content.replies;

import com.microsoft.socialplus.autorest.models.FeedResponseReplyView;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.socialplus.server.exception.NetworkRequestException;
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
	public GetReplyFeedResponse send() throws NetworkRequestException {
		ServiceResponse<FeedResponseReplyView> serviceResponse;
		try {
			serviceResponse = COMMENT_REPLIES.getReplies(commentHandle, authorization,
					getCursor(), getBatchSize());
		} catch (ServiceException|IOException e) {
			throw new NetworkRequestException(e.getMessage());
		}
		checkResponseCode(serviceResponse);

		return new GetReplyFeedResponse(serviceResponse.getBody());
	}
}
