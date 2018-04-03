/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model.account;

import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.UserRequest;
import com.microsoft.embeddedsocial.autorest.models.PutUserInfoRequest;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;

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
	public Response send() throws NetworkRequestException {
		ServiceResponse<Object> serviceResponse;
		try {
			serviceResponse = USERS.putUserInfo(request, authorization);
		} catch (ServiceException|IOException e) {
			throw new NetworkRequestException(e.getMessage());
		}
		checkResponseCode(serviceResponse);
		return serviceResponse.getResponse();
	}
}
