/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model.account;

import com.microsoft.embeddedsocial.autorest.models.UserProfileView;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.UserRequest;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;

import java.io.IOException;

public class GetMyProfileRequest extends UserRequest {

    public GetMyProfileRequest() {

    }

    public GetMyProfileRequest(String authorization) {
        this.authorization = authorization;
    }


    @Override
    public GetUserProfileResponse send() throws NetworkRequestException {
        ServiceResponse<UserProfileView> serviceResponse;
        try {
            serviceResponse = USERS.getMyProfile(authorization);
        } catch (ServiceException|IOException e) {
            throw new NetworkRequestException(e.getMessage());
        }
        checkResponseCode(serviceResponse);

        return new GetUserProfileResponse(serviceResponse.getBody());
    }
}