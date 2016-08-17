/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.relationship;

import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.socialplus.autorest.MyAppFollowingOperations;
import com.microsoft.socialplus.autorest.MyAppFollowingOperationsImpl;
import com.microsoft.socialplus.autorest.models.FeedResponseUserCompactView;
import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.microsoft.socialplus.server.model.FeedUserRequest;
import com.microsoft.socialplus.server.model.UsersListResponse;

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
