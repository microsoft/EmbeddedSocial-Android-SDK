/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model.relationship;

import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.UsersListResponse;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.embeddedsocial.autorest.MyAppFollowingOperations;
import com.microsoft.embeddedsocial.autorest.MyAppFollowingOperationsImpl;
import com.microsoft.embeddedsocial.autorest.models.FeedResponseUserCompactView;
import com.microsoft.embeddedsocial.server.model.FeedUserRequest;

import java.io.IOException;

public class GetFollowingInOtherAppsRequest extends FeedUserRequest {
    private static final MyAppFollowingOperations APP_FOLLOWING_OPERATIONS =
            new MyAppFollowingOperationsImpl(RETROFIT, CLIENT);

    @Override
    public UsersListResponse send() throws NetworkRequestException {
        ServiceResponse<FeedResponseUserCompactView> serviceResponse;
        try {
            serviceResponse = APP_FOLLOWING_OPERATIONS.getUsers(appKey, authorization,
                    getCursor() /*TODO add limit once server is fixed*/);
        } catch (ServiceException|IOException e) {
            throw new NetworkRequestException(e.getMessage());
        }
        checkResponseCode(serviceResponse);

        return new UsersListResponse(serviceResponse.getBody());
    }
}
