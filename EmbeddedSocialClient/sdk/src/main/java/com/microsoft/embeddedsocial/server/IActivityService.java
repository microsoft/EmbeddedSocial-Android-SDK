/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server;

import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.activity.ActivityFeedRequest;
import com.microsoft.embeddedsocial.server.model.activity.ActivityFeedResponse;

public interface IActivityService {
	ActivityFeedResponse getFollowingActivityFeed(ActivityFeedRequest request)
			throws NetworkRequestException;

}
