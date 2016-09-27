/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.blob;

import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.socialplus.autorest.BlobsOperations;
import com.microsoft.socialplus.autorest.BlobsOperationsImpl;
import com.microsoft.socialplus.autorest.models.PostBlobResponse;
import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.microsoft.socialplus.server.model.UserRequest;

import java.io.IOException;

public class AddBlobRequest extends UserRequest {
    private static final BlobsOperations BLOBS = new BlobsOperationsImpl(RETROFIT, CLIENT);

    private byte[] blob;

    public AddBlobRequest(byte[] blob) {
        this.blob = blob;
    }

    @Override
    public String send() throws NetworkRequestException {
        ServiceResponse<PostBlobResponse> serviceResponse;
        try {
            serviceResponse = BLOBS.postBlob(authorization, blob);
        } catch (ServiceException |IOException e) {
            throw new NetworkRequestException(e.getMessage());
        }
        checkResponseCode(serviceResponse);

        return serviceResponse.getBody().getBlobHandle();
    }
}
