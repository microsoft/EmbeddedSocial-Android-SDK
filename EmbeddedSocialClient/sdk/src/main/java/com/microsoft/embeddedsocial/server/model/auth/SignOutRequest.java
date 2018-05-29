/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model.auth;

import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.exception.UnauthorizedException;
import com.microsoft.embeddedsocial.server.model.UserRequest;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;

import java.io.IOException;

import retrofit2.Response;

public class SignOutRequest extends UserRequest {

    public SignOutRequest() {

    }

    public SignOutRequest(String authorization) {
        this.authorization = authorization;
    }

    @Override
    public Response send() throws NetworkRequestException {
        ServiceResponse<Object> serviceResponse;
        try {
            serviceResponse = SESSION.deleteSession(authorization);
        } catch (ServiceException|IOException e) {
            throw new NetworkRequestException(e.getMessage());
        }
        checkResponseCode(serviceResponse);

        return serviceResponse.getResponse();
    }

    @Override
    protected void checkResponseCode(ServiceResponse serviceResponse) throws NetworkRequestException {
        if (serviceResponse.getResponse().code() == 401) { // possible cause: user already signed out
            throw new UnauthorizedException(serviceResponse.getResponse().message());
        } else {
            super.checkResponseCode(serviceResponse);
        }
    }
}
