/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.server;

import com.microsoft.socialplus.autorest.models.GetRequestTokenResponse;
import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.microsoft.socialplus.server.model.auth.AuthenticationResponse;
import com.microsoft.socialplus.server.model.auth.GetRequestTokenRequest;
import com.microsoft.socialplus.server.model.auth.CreateSessionRequest;
import com.microsoft.socialplus.server.model.auth.SignOutRequest;

import retrofit2.Response;

/**
 * Interface for sign-in/sign-out
 */
public interface IAuthenticationService {

	AuthenticationResponse signInWithThirdParty(CreateSessionRequest request)
			throws NetworkRequestException;

	Response signOut(SignOutRequest request)
			throws NetworkRequestException;

	GetRequestTokenResponse getThirdPartyRequestToken(GetRequestTokenRequest request)
		throws NetworkRequestException;
}
