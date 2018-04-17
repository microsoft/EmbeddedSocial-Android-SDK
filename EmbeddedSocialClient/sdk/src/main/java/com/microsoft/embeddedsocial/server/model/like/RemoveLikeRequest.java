/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model.like;

import com.microsoft.embeddedsocial.autorest.models.ContentType;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;

import java.io.IOException;

import retrofit2.Response;

public class RemoveLikeRequest extends GenericLikeRequest {

    public RemoveLikeRequest(String contentHandle, ContentType contentType) {
        super(contentHandle, contentType);
    }

    public Response send() throws NetworkRequestException {
        ServiceResponse<Object> serviceResponse;
        try {
            switch (contentType) {
                case TOPIC:
                    serviceResponse = TOPIC_LIKES.deleteLike(contentHandle, authorization);
                    break;
                case COMMENT:
                    serviceResponse = COMMENT_LIKES.deleteLike(contentHandle, authorization);
                    break;
                case REPLY:
                    serviceResponse = REPLY_LIKES.deleteLike(contentHandle, authorization);
                    break;
                default:
                    throw new IllegalStateException("Unknown type for like");
            }
        } catch (ServiceException|IOException e) {
            throw new NetworkRequestException(e.getMessage());
        }
        checkResponseCode(serviceResponse);

        return serviceResponse.getResponse();
    }
}
