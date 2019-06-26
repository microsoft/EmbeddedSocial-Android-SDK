/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model.account;

import com.microsoft.embeddedsocial.autorest.models.UserProfileView;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.UserRequest;
import es_private.com.microsoft.rest.ServiceException;
import es_private.com.microsoft.rest.ServiceResponse;

import java.io.IOException;

/**
 *
 */
public class GetUserRequest extends UserRequest {

    private String queryUserHandle;

    public GetUserRequest(String queryUserHandle) {
        this.queryUserHandle = queryUserHandle;
    }

    @Override
    public GetUserResponse send() throws NetworkRequestException {
        ServiceResponse<UserProfileView> serviceResponse;
        try {
            serviceResponse = USERS.getUser(queryUserHandle, authorization);
        } catch (ServiceException|IOException e) {
            throw new NetworkRequestException(e.getMessage());
        }
        checkResponseCode(serviceResponse);
        return new GetUserResponse(serviceResponse.getBody());
    }
}
