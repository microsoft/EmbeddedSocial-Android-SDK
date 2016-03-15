/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.content.topics;

import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;

import java.io.IOException;

import retrofit2.Response;

public class HideTopicRequest extends GenericTopicRequest {

    public HideTopicRequest(String topicHandle) {
        super(topicHandle);
    }

    @Override
    public Response send() throws ServiceException, IOException {
        ServiceResponse<Object> serviceResponse =
                FOLLOWING.deleteTopic(topicHandle, bearerToken);
        return serviceResponse.getResponse();
    }
}
