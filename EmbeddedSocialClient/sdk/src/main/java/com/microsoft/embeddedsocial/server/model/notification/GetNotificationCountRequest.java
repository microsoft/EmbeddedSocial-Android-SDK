/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model.notification;

import com.microsoft.embeddedsocial.autorest.models.CountResponse;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.UserRequest;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;

import java.io.IOException;

public class GetNotificationCountRequest extends UserRequest {

    @Override
    public CountResponse send() throws NetworkRequestException {
        ServiceResponse<CountResponse> serviceResponse;
        try {
            serviceResponse = NOTIFICATIONS.getNotificationsCount(authorization);
        } catch (ServiceException |IOException e) {
            throw new NetworkRequestException(e.getMessage());
        }
        checkResponseCode(serviceResponse);

        return serviceResponse.getBody();
    }
}
