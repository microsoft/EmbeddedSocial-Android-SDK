/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.content.comments;

import com.microsoft.socialplus.autorest.models.CommentView;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.socialplus.server.exception.NetworkRequestException;

import java.io.IOException;

public class GetCommentRequest extends GenericCommentRequest {

	public GetCommentRequest(String commentHandle) {
		super(commentHandle);
	}

	@Override
	public GetCommentResponse send() throws NetworkRequestException {
		ServiceResponse<CommentView> serviceResponse;
		try {
			serviceResponse = COMMENTS.getComment(commentHandle, bearerToken);
		} catch (ServiceException|IOException e) {
			throw new NetworkRequestException(e.getMessage());
		}
		checkResponseCode(serviceResponse);

		return new GetCommentResponse(new com.microsoft.socialplus.server.model.view.CommentView(serviceResponse.getBody()));
	}
}
