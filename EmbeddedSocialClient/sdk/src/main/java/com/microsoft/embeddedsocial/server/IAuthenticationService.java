/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server;

import com.microsoft.embeddedsocial.server.model.auth.CreateSessionRequest;
import com.microsoft.embeddedsocial.server.model.auth.SignOutRequest;
import com.microsoft.embeddedsocial.autorest.models.GetRequestTokenResponse;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.auth.AuthenticationResponse;
import com.microsoft.embeddedsocial.server.model.auth.GetRequestTokenRequest;

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
