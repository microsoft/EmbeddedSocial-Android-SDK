/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage;

import com.microsoft.embeddedsocial.server.IBlobService;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.blob.AddBlobRequest;

public class BlobServiceWrapper implements IBlobService {

    @Override
    public String addBlob(AddBlobRequest request) throws NetworkRequestException {
        return request.send();
    }
}
