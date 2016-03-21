package com.microsoft.socialplus.data.storage;

import com.microsoft.socialplus.server.IAuthenticationService;
import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.microsoft.socialplus.server.model.auth.AuthenticationResponse;
import com.microsoft.socialplus.server.model.auth.GetThirdPartyTokenRequest;
import com.microsoft.socialplus.server.model.auth.SignInWithThirdPartyRequest;
import com.microsoft.socialplus.server.model.auth.SignOutRequest;
import com.microsoft.socialplus.server.model.auth.ThirdPartyTokenResponse;

import retrofit2.Response;

public class AuthenticationServiceWrapper implements IAuthenticationService {

    @Override
    public AuthenticationResponse signInWithThirdParty(SignInWithThirdPartyRequest request)
            throws NetworkRequestException {
        return request.send();
    }

    @Override
    public Response signOut(SignOutRequest request) throws NetworkRequestException {
        return request.send();
    }

    @Override
    public ThirdPartyTokenResponse getThirdPartyRequestToken(GetThirdPartyTokenRequest request)
            throws NetworkRequestException {
        return request.send();
    }
}
