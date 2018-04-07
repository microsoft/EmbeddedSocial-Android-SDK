/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model.relationship;

import com.microsoft.embeddedsocial.autorest.models.FollowingStatus;
import com.microsoft.embeddedsocial.autorest.models.PostFollowingUserRequest;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.UserRequest;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;

import java.io.IOException;

public class FollowUserRequest extends UserRelationshipRequest {

    public FollowUserRequest(String relationshipUserHandle) {
        super(relationshipUserHandle);
    }

    @Override
    public FollowUserResponse send() throws NetworkRequestException {
        PostFollowingUserRequest request = new PostFollowingUserRequest();
        request.setUserHandle(relationshipUserHandle);
        ServiceResponse<Object> serviceResponse;
        try {
            serviceResponse = UserRequest.MY_FOLLOWING.postFollowingUser(request, authorization);
        } catch (ServiceException|IOException e) {
            throw new NetworkRequestException(e.getMessage());
        }
        checkResponseCode(serviceResponse);

        return new FollowUserResponse(FollowingStatus.FOLLOW); // TODO fix this logic
    }
}
