package com.microsoft.socialplus.server.model.account;

import com.microsoft.autorest.models.IdentityProvider;
import com.microsoft.autorest.models.PostLinkedAccountRequest;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.socialplus.server.model.UserRequest;

import java.io.IOException;

import retrofit2.Response;

public class LinkThirdPartyRequest extends UserRequest {

    private PostLinkedAccountRequest request;

    public LinkThirdPartyRequest(IdentityProvider identityProvider,
                                 String accessToken) {
        request = new PostLinkedAccountRequest();
        request.setIdentityProvider(identityProvider);
        request.setAccessToken(accessToken);
        //TODO
//        request.setRequestToken(requestToken);
    }

    @Override
    public Response send() throws ServiceException, IOException {
        ServiceResponse<Object> serviceResponse =
                LINKED_ACCOUNTS.postLinkedAccount(request, bearerToken);
        return serviceResponse.getResponse();
    }
}
