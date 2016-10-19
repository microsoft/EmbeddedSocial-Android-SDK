/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.server.model.account;

import com.microsoft.socialplus.autorest.models.LinkedAccountView;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.microsoft.socialplus.server.model.UserRequest;

import java.io.IOException;
import java.util.List;

public class GetLinkedAccountsRequest extends UserRequest {

    @Override
    public List<LinkedAccountView> send() throws NetworkRequestException {
        ServiceResponse<List<LinkedAccountView>> serviceResponse;
        try {
            serviceResponse = LINKED_ACCOUNTS.getLinkedAccounts(authorization);
        } catch (ServiceException|IOException e) {
            throw new NetworkRequestException(e.getMessage());
        }
        checkResponseCode(serviceResponse);

        return serviceResponse.getBody();
    }
}