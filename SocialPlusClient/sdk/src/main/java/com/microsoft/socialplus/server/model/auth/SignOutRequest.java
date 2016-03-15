/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.auth;

import com.microsoft.autorest.models.PostSessionResponse;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.socialplus.server.model.UserRequest;

import java.io.IOException;

import retrofit2.Response;

public class SignOutRequest extends UserRequest {
    @Override
    public Response send() throws ServiceException, IOException {
        ServiceResponse<Object> serviceResponse = SESSION.deleteSession(bearerToken);
        return serviceResponse.getResponse();
    }
}
