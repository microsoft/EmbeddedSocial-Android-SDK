/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.auth;

import com.microsoft.autorest.RequestTokensOperations;
import com.microsoft.autorest.RequestTokensOperationsImpl;
import com.microsoft.autorest.models.GetRequestTokenResponse;
import com.microsoft.autorest.models.IdentityProvider;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.socialplus.server.model.UserRequest;

import java.io.IOException;

public class GetThirdPartyTokenRequest extends UserRequest {

	private static final RequestTokensOperations REQUEST_TOKENS
			= new RequestTokensOperationsImpl(RETROFIT, CLIENT);

	private String identityProvider;

	public GetThirdPartyTokenRequest(IdentityProvider identityProvider) {
		this.identityProvider = identityProvider.toValue();
	}

	@Override
	public ThirdPartyTokenResponse send() throws ServiceException, IOException {
		ServiceResponse<GetRequestTokenResponse> serviceResponse =
				REQUEST_TOKENS.getRequestToken(identityProvider, appKey, bearerToken);
		return new ThirdPartyTokenResponse(serviceResponse.getBody());
	}
}
