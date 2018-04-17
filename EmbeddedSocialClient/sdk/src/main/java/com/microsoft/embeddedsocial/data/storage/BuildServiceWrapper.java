/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage;

import com.microsoft.embeddedsocial.autorest.models.BuildsCurrentResponse;
import com.microsoft.embeddedsocial.server.IBuildService;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.build.GetBuildInfoRequest;

public class BuildServiceWrapper implements IBuildService {

    @Override
    public BuildsCurrentResponse getBuildInfo(GetBuildInfoRequest request) throws NetworkRequestException {
        return request.send();
    }
}
