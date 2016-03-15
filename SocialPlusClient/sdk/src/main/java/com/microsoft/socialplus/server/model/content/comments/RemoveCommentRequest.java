/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.content.comments;

import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;

import java.io.IOException;

import retrofit2.Response;

public class RemoveCommentRequest extends GenericCommentRequest {

	public RemoveCommentRequest(String commentHandle) {
		super(commentHandle);
	}

	@Override
	public Response send() throws ServiceException, IOException {
		ServiceResponse<Object> serviceResponse =
				COMMENTS.deleteComment(commentHandle, bearerToken);
		return serviceResponse.getResponse();
	}
}
