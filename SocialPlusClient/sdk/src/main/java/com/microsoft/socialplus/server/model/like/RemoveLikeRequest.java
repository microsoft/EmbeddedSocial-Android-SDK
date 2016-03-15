/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.like;

import com.microsoft.autorest.models.ContentType;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;

import java.io.IOException;

import retrofit2.Response;

public class RemoveLikeRequest extends GenericLikeRequest {

    public RemoveLikeRequest(String contentHandle, ContentType contentType) {
        super(contentHandle, contentType);
    }

    public Response send() throws ServiceException, IOException {
        ServiceResponse<Object> serviceResponse;
        switch (contentType) {
            case TOPIC:
                serviceResponse = TOPIC_LIKES.deleteLike(contentHandle, bearerToken);
                break;
            case COMMENT:
                serviceResponse = COMMENT_LIKES.deleteLike(contentHandle, bearerToken);
                break;
            case REPLY:
                serviceResponse = REPLY_LIKES.deleteLike(contentHandle, bearerToken);
                break;
            default:
                throw new IllegalStateException("Unknown type for like");
        }
        return serviceResponse.getResponse();
    }
}
