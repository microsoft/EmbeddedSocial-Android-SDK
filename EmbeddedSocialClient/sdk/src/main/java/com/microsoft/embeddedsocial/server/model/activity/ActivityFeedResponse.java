/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model.activity;

import com.microsoft.embeddedsocial.autorest.models.FeedResponseActivityView;
import com.microsoft.embeddedsocial.server.model.FeedUserResponse;
import com.microsoft.embeddedsocial.server.model.ListResponse;
import com.microsoft.embeddedsocial.server.model.view.ActivityView;

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
		for (com.microsoft.embeddedsocial.autorest.models.ActivityView view : response.getData()) {
			activities.add(new ActivityView(view));
		}
		setContinuationKey(response.getCursor());
	}

	@Override
	public List<ActivityView> getData() {
		return activities;
	}
}
