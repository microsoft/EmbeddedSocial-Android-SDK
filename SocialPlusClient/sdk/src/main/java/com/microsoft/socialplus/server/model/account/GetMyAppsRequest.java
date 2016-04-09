/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.account;

import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.socialplus.autorest.MyAppsOperations;
import com.microsoft.socialplus.autorest.MyAppsOperationsImpl;
import com.microsoft.socialplus.autorest.models.AppCompactView;
import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.microsoft.socialplus.server.model.FeedUserRequest;

import java.io.IOException;
import java.util.List;


public class GetMyAppsRequest extends FeedUserRequest {
    private static final MyAppsOperations MY_APPS =
            new MyAppsOperationsImpl(RETROFIT, CLIENT);

    @Override
    public List<AppCompactView> send() throws NetworkRequestException {
        ServiceResponse<List<AppCompactView>> serviceResponse;
        try {
            serviceResponse = MY_APPS.getApps(bearerToken /* TODO add cursor and limit when server side fixed*/);
        } catch (ServiceException |IOException e) {
            throw new NetworkRequestException(e.getMessage());
        }
        checkResponseCode(serviceResponse);
        return serviceResponse.getBody();
    }
}
