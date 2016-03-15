/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.account;

import com.microsoft.autorest.models.PutUserPhotoRequest;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.socialplus.server.model.UserRequest;

import java.io.IOException;

import retrofit2.Response;

/**
 *
 */
public class UpdateUserPhotoRequest extends UserRequest {

	private PutUserPhotoRequest request;

	public UpdateUserPhotoRequest(String userPhotoHandle) {
		request = new PutUserPhotoRequest();
		request.setPhotoHandle(userPhotoHandle);
	}

	@Override
	public Response send() throws ServiceException, IOException {
		ServiceResponse<Object> serviceResponse = USERS.putUserPhoto(request, bearerToken);
		return serviceResponse.getResponse();
	}
}
