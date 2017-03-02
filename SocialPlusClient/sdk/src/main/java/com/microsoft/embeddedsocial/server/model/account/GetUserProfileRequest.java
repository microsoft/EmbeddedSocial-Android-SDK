/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model.account;

import com.microsoft.embeddedsocial.autorest.models.UserProfileView;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.UserRequest;

import java.io.IOException;

public class GetUserProfileRequest extends UserRequest {

	private final String queryUserHandle;
	private final String queryUsername;

	public GetUserProfileRequest(String queryUserHandle) {
		this(queryUserHandle, null);
	}

	public GetUserProfileRequest(String queryUserHandle, String queryUsername) {
		this.queryUserHandle = queryUserHandle;
		this.queryUsername = queryUsername;
	}

	public String getQueryUserHandle() {
		return queryUserHandle;
	}

	@Override
	public GetUserProfileResponse send() throws NetworkRequestException {
		ServiceResponse<UserProfileView> serviceResponse;
		try {
			serviceResponse = USERS.getUser(queryUserHandle, authorization);
		} catch (ServiceException|IOException e) {
			throw new NetworkRequestException(e.getMessage());
		}
		checkResponseCode(serviceResponse);

		return new GetUserProfileResponse(serviceResponse.getBody());
	}
}
