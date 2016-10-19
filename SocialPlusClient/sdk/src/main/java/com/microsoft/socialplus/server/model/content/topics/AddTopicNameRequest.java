/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.server.model.content.topics;

import com.microsoft.socialplus.autorest.models.PostTopicNameRequest;
import com.microsoft.socialplus.autorest.models.PublisherType;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.microsoft.socialplus.server.model.UserRequest;

import java.io.IOException;

import retrofit2.Response;

public class AddTopicNameRequest extends UserRequest {

    private final PostTopicNameRequest requestBody;

    public AddTopicNameRequest(String topicHandle, String topicName, PublisherType publisherType) {
        requestBody = new PostTopicNameRequest();
        requestBody.setTopicHandle(topicHandle);
        requestBody.setTopicName(topicName);
        requestBody.setPublisherType(publisherType);
    }

    @Override
    public Response send() throws NetworkRequestException {
        ServiceResponse<Object> serviceResponse;
        try {
            serviceResponse = TOPICS.postTopicName(requestBody, authorization);
        } catch (ServiceException|IOException e) {
            throw new NetworkRequestException(e.getMessage());
        }
        checkResponseCode(serviceResponse);

        return serviceResponse.getResponse();
    }
}
