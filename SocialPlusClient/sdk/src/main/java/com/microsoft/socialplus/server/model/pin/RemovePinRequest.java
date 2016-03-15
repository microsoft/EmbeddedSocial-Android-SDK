/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.pin;

import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.socialplus.base.utils.ObjectUtils;

import java.io.IOException;

import retrofit2.Response;

public class RemovePinRequest extends GenericPinRequest {

    public RemovePinRequest(String topicHandle) {
        super(topicHandle);
    }

    @Override
    public Response send() throws ServiceException, IOException {
        ServiceResponse<Object> serviceResponse = PINS.deletePin(topicHandle, bearerToken);
        return serviceResponse.getResponse();
    }
}
