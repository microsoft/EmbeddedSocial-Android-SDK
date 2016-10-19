/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.server.model.notification;

import com.microsoft.socialplus.autorest.models.FeedResponseActivityView;
import com.microsoft.socialplus.server.model.FeedUserResponse;
import com.microsoft.socialplus.server.model.ListResponse;
import com.microsoft.socialplus.server.model.view.ActivityView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GetNotificationFeedResponse extends FeedUserResponse implements ListResponse<ActivityView> {

	private List<ActivityView> activities;
	private String deliveredActivityHandle;

	public GetNotificationFeedResponse(List<ActivityView> activities) {
		this.activities = activities;

		// FIXME fix setting this handle
		this.deliveredActivityHandle = "";
	}

	public GetNotificationFeedResponse (FeedResponseActivityView response) {
		activities = new ArrayList<>();
		for (com.microsoft.socialplus.autorest.models.ActivityView view : response.getData()) {
			activities.add(new ActivityView(view));
		}
		this.deliveredActivityHandle = "";
	}

	@Override
	public List<ActivityView> getData() {
		return activities != null ? activities : Collections.emptyList();
	}

	public String getDeliveredActivityHandle() {
		return deliveredActivityHandle;
	}
}
