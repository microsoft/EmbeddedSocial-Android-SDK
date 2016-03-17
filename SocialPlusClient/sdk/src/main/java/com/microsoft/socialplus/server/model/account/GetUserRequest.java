/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.account;

import com.microsoft.autorest.models.UserProfileView;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.socialplus.server.model.UserRequest;

import java.io.IOException;

/**
 *
 */
public class GetUserRequest extends UserRequest {

	private String queryUserHandle;

	public GetUserRequest(String queryUserHandle) {
		this.queryUserHandle = queryUserHandle;
	}

	@Override
	public GetUserResponse send() throws ServiceException, IOException {
		ServiceResponse<UserProfileView> serviceResponse =
				USERS.getUser(queryUserHandle, appKey, bearerToken);
		return new GetUserResponse(serviceResponse.getBody());
	}
}
