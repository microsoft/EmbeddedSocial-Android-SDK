/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server;

import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.microsoft.socialplus.server.model.auth.AuthenticationResponse;
import com.microsoft.socialplus.server.model.auth.GetThirdPartyTokenRequest;
import com.microsoft.socialplus.server.model.auth.SignInWithThirdPartyRequest;
import com.microsoft.socialplus.server.model.auth.SignOutRequest;
import com.microsoft.socialplus.server.model.auth.ThirdPartyTokenResponse;

import retrofit2.Response;

/**
 * Interface for sign-in/sign-out
 */
public interface IAuthenticationService {

	AuthenticationResponse signInWithThirdParty(SignInWithThirdPartyRequest request)
			throws NetworkRequestException;

	Response signOut(SignOutRequest request)
			throws NetworkRequestException;

	ThirdPartyTokenResponse getThirdPartyRequestToken(GetThirdPartyTokenRequest request)
		throws NetworkRequestException;
}
