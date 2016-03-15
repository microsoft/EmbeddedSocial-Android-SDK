/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.activity;

import com.microsoft.autorest.models.FeedResponseActivityView;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.socialplus.server.model.FeedUserRequest;

import java.io.IOException;

public class ActivityFeedRequest extends FeedUserRequest {

    @Override
    public ActivityFeedResponse send() throws ServiceException, IOException {
        ServiceResponse<FeedResponseActivityView> serviceResponse =
                FOLLOWING.getActivities(bearerToken, getCursor(), getBatchSize());
        return new ActivityFeedResponse(serviceResponse.getBody());
    }
}
