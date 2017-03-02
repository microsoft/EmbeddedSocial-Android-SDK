/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model.pin;

import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.content.topics.TopicsListResponse;
import com.microsoft.embeddedsocial.autorest.models.FeedResponseTopicView;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.embeddedsocial.server.model.FeedUserRequest;

import java.io.IOException;

public class GetPinFeedRequest extends FeedUserRequest {

    @Override
    public TopicsListResponse send() throws NetworkRequestException {
        ServiceResponse<FeedResponseTopicView> serviceResponse;
        try {
            serviceResponse = PINS.getPins(authorization, getCursor(), getBatchSize());
        } catch (ServiceException|IOException e) {
            throw new NetworkRequestException(e.getMessage());
        }
        checkResponseCode(serviceResponse);

        return new TopicsListResponse(serviceResponse.getBody());
    }
}
