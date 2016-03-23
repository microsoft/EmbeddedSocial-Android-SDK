/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.pin;

import com.microsoft.socialplus.autorest.models.PostPinRequest;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.socialplus.base.utils.ObjectUtils;
import com.microsoft.socialplus.server.exception.NetworkRequestException;

import java.io.IOException;

import retrofit2.Response;

public class AddPinRequest extends GenericPinRequest {

    public AddPinRequest(String topicHandle) {
        super(topicHandle);
    }

    @Override
    public Response send() throws NetworkRequestException {
        PostPinRequest request = new PostPinRequest();
        request.setTopicHandle(topicHandle);
        ServiceResponse<Object> serviceResponse;
        try {
            serviceResponse = PINS.postPin(request, bearerToken);
        } catch (ServiceException|IOException e) {
            throw new NetworkRequestException(e.getMessage());
        }
        checkResponseCode(serviceResponse);

        return serviceResponse.getResponse();
    }
}
