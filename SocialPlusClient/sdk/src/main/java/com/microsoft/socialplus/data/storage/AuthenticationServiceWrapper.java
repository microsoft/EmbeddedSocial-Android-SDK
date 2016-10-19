/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.data.storage;

import com.microsoft.socialplus.autorest.models.GetRequestTokenResponse;
import com.microsoft.socialplus.server.IAuthenticationService;
import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.microsoft.socialplus.server.model.auth.AuthenticationResponse;
import com.microsoft.socialplus.server.model.auth.GetRequestTokenRequest;
import com.microsoft.socialplus.server.model.auth.CreateSessionRequest;
import com.microsoft.socialplus.server.model.auth.SignOutRequest;

import retrofit2.Response;

public class AuthenticationServiceWrapper implements IAuthenticationService {

    @Override
    public AuthenticationResponse signInWithThirdParty(CreateSessionRequest request)
            throws NetworkRequestException {
        return request.send();
    }

    @Override
    public Response signOut(SignOutRequest request) throws NetworkRequestException {
        return request.send();
    }

    @Override
    public GetRequestTokenResponse getThirdPartyRequestToken(GetRequestTokenRequest request)
            throws NetworkRequestException {
        return request.send();
    }
}
