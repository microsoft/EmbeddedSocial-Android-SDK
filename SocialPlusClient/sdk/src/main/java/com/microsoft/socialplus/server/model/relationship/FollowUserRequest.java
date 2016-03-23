/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.relationship;

import com.microsoft.socialplus.autorest.models.FollowingStatus;
import com.microsoft.socialplus.autorest.models.PostBlockedUserRequest;
import com.microsoft.socialplus.autorest.models.PostFollowingRequest;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.socialplus.server.exception.NetworkRequestException;

import java.io.IOException;

import retrofit2.Response;

public class FollowUserRequest extends UserRelationshipRequest {

    public FollowUserRequest(String relationshipUserHandle) {
        super(relationshipUserHandle);
    }

    @Override
    public FollowUserResponse send() throws NetworkRequestException {
        PostFollowingRequest request = new PostFollowingRequest();
        request.setUserHandle(relationshipUserHandle);
        ServiceResponse<Object> serviceResponse;
        try {
            serviceResponse = FOLLOWING.postFollowing(request, bearerToken);
        } catch (ServiceException|IOException e) {
            throw new NetworkRequestException(e.getMessage());
        }
        checkResponseCode(serviceResponse);

        return new FollowUserResponse(FollowingStatus.FOLLOW); // TODO fix this logic
    }
}
