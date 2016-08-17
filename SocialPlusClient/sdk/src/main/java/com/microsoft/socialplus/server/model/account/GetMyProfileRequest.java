/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.account;

import com.microsoft.socialplus.autorest.models.UserProfileView;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.microsoft.socialplus.server.model.UserRequest;

import java.io.IOException;

public class GetMyProfileRequest extends UserRequest {

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