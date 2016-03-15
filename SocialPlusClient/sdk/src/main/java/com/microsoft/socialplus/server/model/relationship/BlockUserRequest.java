/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.relationship;

import com.microsoft.autorest.models.PostBlockedUserRequest;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;

import java.io.IOException;

import retrofit2.Response;

public class BlockUserRequest extends UserRelationshipRequest {

    public BlockUserRequest(String relationshipUserHandle) {
        super(relationshipUserHandle);
    }

    @Override
    public Response send() throws ServiceException, IOException {
        PostBlockedUserRequest request = new PostBlockedUserRequest();
        request.setUserHandle(relationshipUserHandle);
        ServiceResponse<Object> serviceResponse = BLOCKED.postBlockedUser(request, bearerToken);
        return serviceResponse.getResponse();
    }
}
