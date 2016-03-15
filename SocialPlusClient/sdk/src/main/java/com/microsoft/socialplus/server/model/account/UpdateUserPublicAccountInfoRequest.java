/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.account;

import com.microsoft.autorest.models.PutUserInfoRequest;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.socialplus.server.model.UserRequest;

import java.io.IOException;

import retrofit2.Response;

/**
 *
 */
public class UpdateUserPublicAccountInfoRequest extends UserRequest {

	private PutUserInfoRequest request;

	public UpdateUserPublicAccountInfoRequest(String firstName, String lastName, String bio) {
		request = new PutUserInfoRequest();
		request.setFirstName(firstName);
		request.setLastName(lastName);
		request.setBio(bio);
	}

	@Override
	public Response send() throws ServiceException, IOException {
		ServiceResponse<Object> serviceResponse = USERS.putUserInfo(request, bearerToken);
		return serviceResponse.getResponse();
	}
}
