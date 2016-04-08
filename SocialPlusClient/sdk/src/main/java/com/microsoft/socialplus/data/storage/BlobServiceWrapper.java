/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.data.storage;

import com.microsoft.socialplus.server.IBlobService;
import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.microsoft.socialplus.server.model.blob.AddBlobRequest;

public class BlobServiceWrapper implements IBlobService {

    @Override
    public String addBlob(AddBlobRequest request) throws NetworkRequestException {
        return request.send();
    }
}
