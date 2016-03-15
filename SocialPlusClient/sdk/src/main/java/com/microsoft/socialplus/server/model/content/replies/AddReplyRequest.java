/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.content.replies;

import com.microsoft.autorest.models.PostReplyRequest;
import com.microsoft.autorest.models.PostReplyResponse;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.socialplus.server.model.UserRequest;

import java.io.IOException;

public class AddReplyRequest extends UserRequest {

	private final String commentHandle;
	private final PostReplyRequest request;

	public AddReplyRequest(String commentHandle, String contentText) {
		this.commentHandle = commentHandle;
		request = new PostReplyRequest();
		request.setText(contentText);
		request.setLanguage(language);
	}

	@Override
	public AddReplyResponse send() throws ServiceException, IOException {
		ServiceResponse<PostReplyResponse> serviceResponse =
				COMMENT_REPLIES.postReply(commentHandle, request, bearerToken);
		return new AddReplyResponse(serviceResponse.getBody().getReplyHandle());
	}
}
