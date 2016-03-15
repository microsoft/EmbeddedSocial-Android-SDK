/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.relationship;

import com.microsoft.autorest.models.FollowingStatus;
import com.microsoft.autorest.models.PostBlockedUserRequest;
import com.microsoft.autorest.models.PostFollowingRequest;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;

import java.io.IOException;

import retrofit2.Response;

public class FollowUserRequest extends UserRelationshipRequest {

    public FollowUserRequest(String relationshipUserHandle) {
        super(relationshipUserHandle);
    }

    @Override
    public FollowUserResponse send() throws ServiceException, IOException {
        PostFollowingRequest request = new PostFollowingRequest();
        request.setUserHandle(relationshipUserHandle);
        ServiceResponse<Object> serviceResponse = FOLLOWING.postFollowing(request, bearerToken);
        return new FollowUserResponse(FollowingStatus.FOLLOW); // TODO fix this logic
    }
}
