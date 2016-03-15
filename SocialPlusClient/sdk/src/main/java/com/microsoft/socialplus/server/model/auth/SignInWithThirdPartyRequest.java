/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.auth;

import com.microsoft.autorest.models.IdentityProvider;
import com.microsoft.autorest.models.PostSessionRequest;
import com.microsoft.autorest.models.PostSessionResponse;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.socialplus.base.GlobalObjectRegistry;
import com.microsoft.socialplus.sdk.Options;
import com.microsoft.socialplus.server.model.UserRequest;

import java.io.IOException;

/**
 *
 */
public class SignInWithThirdPartyRequest extends UserRequest {
	//TODO Fix constant value
	private final String socialPlusAppToken = GlobalObjectRegistry.getObject(Options.class).getAppToken();

	private final PostSessionRequest request;

	public SignInWithThirdPartyRequest(
			IdentityProvider identityProvider,
			String thirdPartyAccessToken) {
		request = new PostSessionRequest();
		request.setIdentityProvider(identityProvider);
		request.setAccessToken(thirdPartyAccessToken);
		request.setInstanceId(instanceID);
		request.setCreateUser(false);
	}

	@Override
	public AuthenticationResponse send() throws ServiceException, IOException {
		ServiceResponse<PostSessionResponse> serviceResponse =
				SESSION.postSession(request, appKey, bearerToken);
		return new AuthenticationResponse(serviceResponse.getBody());
	}
}
