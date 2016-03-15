/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.pin;

import com.microsoft.autorest.models.FeedResponseTopicView;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.socialplus.server.model.FeedUserRequest;
import com.microsoft.socialplus.server.model.content.topics.TopicsListResponse;

import java.io.IOException;

public class GetPinFeedRequest extends FeedUserRequest {

    @Override
    public TopicsListResponse send() throws ServiceException, IOException {
        ServiceResponse<FeedResponseTopicView> serviceResponse =
                PINS.getPins(bearerToken, getCursor(), getBatchSize());
        return new TopicsListResponse(serviceResponse.getBody());
    }
}
