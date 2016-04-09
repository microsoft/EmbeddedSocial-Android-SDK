/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.relationship;

import com.microsoft.socialplus.autorest.models.PostFollowerRequest;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.socialplus.server.exception.NetworkRequestException;

import java.io.IOException;

import retrofit2.Response;

public class AcceptFollowRequest extends UserRelationshipRequest {

    public AcceptFollowRequest(String relationshipUserHandle) {
        super(relationshipUserHandle);
    }

    @Override
    public Response send() throws NetworkRequestException {
        PostFollowerRequest request = new PostFollowerRequest();
        request.setUserHandle(relationshipUserHandle);
        ServiceResponse<Object> serviceResponse;
        try {
            serviceResponse = MY_FOLLOWERS.postFollower(request, bearerToken);
        } catch (ServiceException|IOException e) {
            throw new NetworkRequestException(e.getMessage());
        }
        checkResponseCode(serviceResponse);

        return serviceResponse.getResponse();
    }
}
