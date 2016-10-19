/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.data.storage;

import com.microsoft.socialplus.autorest.models.BuildsCurrentResponse;
import com.microsoft.socialplus.server.IBuildService;
import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.microsoft.socialplus.server.model.build.GetBuildInfoRequest;

public class BuildServiceWrapper implements IBuildService {

    @Override
    public BuildsCurrentResponse getBuildInfo(GetBuildInfoRequest request) throws NetworkRequestException {
        return request.send();
    }
}
