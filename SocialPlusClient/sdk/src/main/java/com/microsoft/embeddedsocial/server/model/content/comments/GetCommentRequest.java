/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model.content.comments;

import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.autorest.models.CommentView;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;

import java.io.IOException;

public class GetCommentRequest extends GenericCommentRequest {

	public GetCommentRequest(String commentHandle) {
		super(commentHandle);
	}

	@Override
	public GetCommentResponse send() throws NetworkRequestException {
		ServiceResponse<CommentView> serviceResponse;
		try {
			serviceResponse = COMMENTS.getComment(commentHandle, authorization);
		} catch (ServiceException|IOException e) {
			throw new NetworkRequestException(e.getMessage());
		}
		checkResponseCode(serviceResponse);

		return new GetCommentResponse(new com.microsoft.embeddedsocial.server.model.view.CommentView(serviceResponse.getBody()));
	}
}
