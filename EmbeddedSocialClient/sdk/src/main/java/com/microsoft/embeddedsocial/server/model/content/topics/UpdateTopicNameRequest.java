/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model.content.topics;

import com.microsoft.embeddedsocial.autorest.models.PublisherType;
import com.microsoft.embeddedsocial.autorest.models.PutTopicNameRequest;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.UserRequest;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;

import java.io.IOException;

import retrofit2.Response;

public class UpdateTopicNameRequest extends UserRequest {

    private final String topicName;
    private final PutTopicNameRequest requestBody;

    public UpdateTopicNameRequest(String topicHandle, String topicName, PublisherType publisherType) {
        this.topicName = topicName;
        requestBody = new PutTopicNameRequest();
        requestBody.setTopicHandle(topicHandle);
        requestBody.setPublisherType(publisherType);
    }

    @Override
    public Response send() throws NetworkRequestException {
        ServiceResponse<Object> serviceResponse;
        try {
            serviceResponse = TOPICS.putTopicName(topicName, requestBody, authorization);
        } catch (ServiceException|IOException e) {
            throw new NetworkRequestException(e.getMessage());
        }
        checkResponseCode(serviceResponse);

        return serviceResponse.getResponse();
    }
}
