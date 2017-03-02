/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model.auth;

import com.microsoft.embeddedsocial.autorest.models.IdentityProvider;
import com.microsoft.embeddedsocial.autorest.models.PostSessionRequest;
import com.microsoft.embeddedsocial.autorest.models.PostSessionResponse;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.UserRequest;

import java.io.IOException;

/**
 *
 */
public class CreateSessionRequest extends UserRequest {

	private final PostSessionRequest request;

	public CreateSessionRequest(IdentityProvider identityProvider, String accessToken, String requestToken) {
		request = new PostSessionRequest();
		request.setUserHandle(getUserHandle());
		request.setInstanceId(instanceId);

		authorization = createThirdPartyAuthorization(identityProvider, accessToken, requestToken);
	}

	public void setRequestUserHandle(String userHandle) {
		request.setUserHandle(userHandle);
	}

	@Override
	public AuthenticationResponse send() throws NetworkRequestException {
		ServiceResponse<PostSessionResponse> serviceResponse;
		try {
			serviceResponse = SESSION.postSession(request, authorization);
        } catch (ServiceException|IOException e) {
			throw new NetworkRequestException(e.getMessage());
		}

		checkResponseCode(serviceResponse);

        return new AuthenticationResponse(serviceResponse.getBody());
    }
}
