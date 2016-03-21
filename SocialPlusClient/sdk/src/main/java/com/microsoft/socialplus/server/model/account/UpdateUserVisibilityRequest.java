/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.account;

import com.microsoft.autorest.models.PutUserVisibilityRequest;
import com.microsoft.autorest.models.Visibility;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.microsoft.socialplus.server.model.UserRequest;

import java.io.IOException;

import retrofit2.Response;

/**
 *
 */
public class UpdateUserVisibilityRequest extends UserRequest {

	private PutUserVisibilityRequest request;

	public UpdateUserVisibilityRequest(Visibility visibility) {
		request = new PutUserVisibilityRequest();
		request.setVisibility(visibility);
	}

	@Override
	public Response send() throws NetworkRequestException {
		ServiceResponse<Object> serviceResponse;
		try {
			serviceResponse = USERS.putUserVisibility(request, bearerToken);
		} catch (ServiceException|IOException e) {
			throw new NetworkRequestException(e.getMessage());
		}
		checkResponseCode(serviceResponse);
		return serviceResponse.getResponse();
	}
}
