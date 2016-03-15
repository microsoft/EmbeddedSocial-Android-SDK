/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.notification;

import com.microsoft.autorest.models.CountResponse;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.socialplus.server.model.UserRequest;

import java.io.IOException;

public class GetNotificationCountRequest extends UserRequest {

    @Override
    public CountResponse send() throws ServiceException, IOException {
        ServiceResponse<CountResponse> serviceResponse =
                NOTIFICATIONS.getNotificationsCount(bearerToken);
        return serviceResponse.getBody();
    }
}
