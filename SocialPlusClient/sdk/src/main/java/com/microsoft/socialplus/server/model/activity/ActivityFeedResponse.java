/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.activity;

import com.microsoft.autorest.models.FeedResponseActivityView;
import com.microsoft.socialplus.server.model.FeedUserResponse;
import com.microsoft.socialplus.server.model.ListResponse;
import com.microsoft.socialplus.server.model.view.ActivityView;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ActivityFeedResponse extends FeedUserResponse implements ListResponse<ActivityView> {

	private List<ActivityView> activities;

	public ActivityFeedResponse(List<ActivityView> activities) {
		this.activities = activities;
	}

	public ActivityFeedResponse(FeedResponseActivityView response) {
		activities = new ArrayList<>();
		for (com.microsoft.autorest.models.ActivityView view : response.getData()) {
			activities.add(new ActivityView(view));
		}
	}

	@Override
	public List<ActivityView> getData() {
		return activities;
	}
}
