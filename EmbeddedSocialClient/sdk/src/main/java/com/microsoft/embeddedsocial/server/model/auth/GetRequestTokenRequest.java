/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model.auth;

import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.autorest.RequestTokensOperations;
import com.microsoft.embeddedsocial.autorest.RequestTokensOperationsImpl;
import com.microsoft.embeddedsocial.autorest.models.GetRequestTokenResponse;
import com.microsoft.embeddedsocial.autorest.models.IdentityProvider;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.embeddedsocial.server.model.UserRequest;

import java.io.IOException;

public class GetRequestTokenRequest extends UserRequest {

	private static final RequestTokensOperations REQUEST_TOKENS
			= new RequestTokensOperationsImpl(RETROFIT, CLIENT);

	private IdentityProvider identityProvider;

	public GetRequestTokenRequest(IdentityProvider identityProvider) {
		this.identityProvider = identityProvider;
	}

	@Override
	public GetRequestTokenResponse send() throws NetworkRequestException {
		ServiceResponse<GetRequestTokenResponse> serviceResponse;
		try {
			serviceResponse = REQUEST_TOKENS.getRequestToken(identityProvider, authorization);
		} catch (ServiceException|IOException e) {
			throw new NetworkRequestException(e.getMessage());
		}
		checkResponseCode(serviceResponse);

		return serviceResponse.getBody();
	}
}
