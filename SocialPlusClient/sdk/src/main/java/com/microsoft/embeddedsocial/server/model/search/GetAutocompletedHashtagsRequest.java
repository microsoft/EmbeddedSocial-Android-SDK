/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model.search;

import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.UserRequest;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;

import java.io.IOException;
import java.util.List;

public class GetAutocompletedHashtagsRequest extends SearchRequest {

    public GetAutocompletedHashtagsRequest(String query) {
        super(query);
    }

    @Override
    public AutocompletedHashtagsResponse send() throws NetworkRequestException {
        ServiceResponse<List<String>> serviceResponse;
        try {
            serviceResponse = UserRequest.HASHTAGS.getAutocompletedHashtags(query, authorization);
        } catch (ServiceException|IOException e) {
            throw new NetworkRequestException(e.getMessage());
        }
        checkResponseCode(serviceResponse);

        return new AutocompletedHashtagsResponse(serviceResponse.getBody());
    }
}
