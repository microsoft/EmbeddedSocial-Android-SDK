/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.server.model.relationship;

import com.microsoft.socialplus.server.model.FeedUserRequest;

public class GetFollowFeedRequest extends FeedUserRequest {
    private final String queryUserHandle;

    public GetFollowFeedRequest(String queryUserHandle) {
        this.queryUserHandle = queryUserHandle;
    }

    public String getQueryUserHandle() {
        return queryUserHandle;
    }
}
