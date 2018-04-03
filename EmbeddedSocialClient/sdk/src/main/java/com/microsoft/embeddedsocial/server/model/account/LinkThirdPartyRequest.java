/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model.account;

import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.autorest.models.IdentityProvider;
import com.microsoft.embeddedsocial.autorest.models.PostLinkedAccountRequest;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.embeddedsocial.server.model.UserRequest;

import java.io.IOException;

import retrofit2.Response;

public class LinkThirdPartyRequest extends UserRequest {

    private PostLinkedAccountRequest request;

    public LinkThirdPartyRequest(IdentityProvider identityProvider,
                                 String accessToken) {
        request = new PostLinkedAccountRequest();
        request.setSessionToken(accessToken);
        //TODO
//        request.setRequestToken(requestToken);
    }

    @Override
    public Response send() throws NetworkRequestException {
        ServiceResponse<Object> serviceResponse;
        try {
            serviceResponse = LINKED_ACCOUNTS.postLinkedAccount(request, authorization);
        } catch (ServiceException|IOException e) {
            throw new NetworkRequestException(e.getMessage());
        }
        checkResponseCode(serviceResponse);
        return serviceResponse.getResponse();
    }
}
