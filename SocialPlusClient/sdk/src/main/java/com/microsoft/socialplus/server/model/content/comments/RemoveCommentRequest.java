/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.content.comments;

import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.socialplus.server.exception.NetworkRequestException;

import java.io.IOException;

import retrofit2.Response;

public class RemoveCommentRequest extends GenericCommentRequest {

	public RemoveCommentRequest(String commentHandle) {
		super(commentHandle);
	}

	@Override
	public Response send() throws NetworkRequestException {
		ServiceResponse<Object> serviceResponse;
		try {
			serviceResponse = COMMENTS.deleteComment(commentHandle, authorization);
		} catch (ServiceException|IOException e) {
			throw new NetworkRequestException(e.getMessage());
		}
		checkResponseCode(serviceResponse);

		return serviceResponse.getResponse();
	}
}
