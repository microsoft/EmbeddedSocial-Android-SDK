/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.relationship;

import com.microsoft.autorest.models.PostFollowerRequest;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;

import java.io.IOException;

import retrofit2.Response;

public class AcceptFollowRequest extends UserRelationshipRequest {

    public AcceptFollowRequest(String relationshipUserHandle) {
        super(relationshipUserHandle);
    }

    @Override
    public Response send() throws ServiceException, IOException {
        PostFollowerRequest request = new PostFollowerRequest();
        request.setUserHandle(relationshipUserHandle);
        ServiceResponse<Object> serviceResponse = FOLLOWERS.postFollower(request, bearerToken);
        return serviceResponse.getResponse();
    }
}
