/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage;

import android.content.Context;

import com.microsoft.embeddedsocial.data.storage.request.wrapper.AbstractBatchRequestWrapper;
import com.microsoft.embeddedsocial.server.IActivityService;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.activity.ActivityFeedRequest;
import com.microsoft.embeddedsocial.server.model.activity.ActivityFeedResponse;

import java.sql.SQLException;

/**
 * Provides transparent cache implementation of top of {@linkplain IActivityService}.
 */
public class ActivityServiceCachingWrapper implements IActivityService {

	private final FollowingActivityFeedWrapper followingActivityFeedWrapper
		= new FollowingActivityFeedWrapper();

	private final ActivityCache activityCache;

	public ActivityServiceCachingWrapper(Context context) {
		this.activityCache = new ActivityCache(context);
	}

	@Override
	public ActivityFeedResponse getFollowingActivityFeed(ActivityFeedRequest request) throws NetworkRequestException {
		return followingActivityFeedWrapper.getResponse(request);
	}

	private class FollowingActivityFeedWrapper extends AbstractBatchRequestWrapper<ActivityFeedRequest, ActivityFeedResponse> {

		@Override
		protected ActivityFeedResponse getNetworkResponse(ActivityFeedRequest request)
			throws NetworkRequestException {

			return request.send();
		}

		@Override
		protected void storeResponse(ActivityFeedRequest request, ActivityFeedResponse response)
			throws SQLException {

			activityCache.storeActivityFeed(ActivityCache.ActivityFeedType.FOLLOWING_ACTIVITY,
				response.getData(), isFirstDataRequest(request));
		}

		@Override
		protected ActivityFeedResponse getCachedResponse(ActivityFeedRequest request)
			throws SQLException {

			return activityCache.getActivityFeedResponse(ActivityCache.ActivityFeedType.FOLLOWING_ACTIVITY);
		}
	}
}
