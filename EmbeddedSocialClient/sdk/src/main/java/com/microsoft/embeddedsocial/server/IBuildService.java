/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server;

import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.build.GetBuildInfoRequest;
import com.microsoft.embeddedsocial.autorest.models.BuildsCurrentResponse;

public interface IBuildService {

    BuildsCurrentResponse getBuildInfo(GetBuildInfoRequest request) throws NetworkRequestException;
}
