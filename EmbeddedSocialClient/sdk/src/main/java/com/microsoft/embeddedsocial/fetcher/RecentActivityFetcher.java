/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.fetcher;

import com.microsoft.embeddedsocial.base.function.Predicate;
import com.microsoft.embeddedsocial.fetcher.base.RequestType;
import com.microsoft.embeddedsocial.server.ServerMethod;
import com.microsoft.embeddedsocial.server.model.activity.ActivityFeedRequest;
import com.microsoft.embeddedsocial.server.model.view.ActivityView;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.fetcher.base.DataState;
import com.microsoft.embeddedsocial.fetcher.base.Fetcher;
import com.microsoft.embeddedsocial.server.model.ListResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Fetches data for the recent activity page. It filters items the app is not ready for (incorrect data can crash the app or corrupt UI).
 */
class RecentActivityFetcher extends Fetcher<ActivityView> {

	private final DataRequestExecutor<ActivityView, ActivityFeedRequest> activityRequestExecutor;
	private final Predicate<ActivityView> dataFilter;

	RecentActivityFetcher(ServerMethod<ActivityFeedRequest, ListResponse<ActivityView>> serverMethod, Predicate<ActivityView> dataFilter) {
		activityRequestExecutor = new BatchDataRequestExecutor<>(serverMethod, ActivityFeedRequest::new);
		this.dataFilter = dataFilter;
	}

	@Override
	protected List<ActivityView> fetchDataPage(DataState dataState, RequestType requestType, int pageSize) throws Exception {
		List<ActivityView> result = new ArrayList<>(pageSize);
		List<ActivityView> events = activityRequestExecutor.fetchData(dataState, requestType, pageSize);
		for (ActivityView event : events) {
			if (dataFilter.test(event)) {
				result.add(event);
			} else {
				DebugLog.e("invalid activity event");
				DebugLog.logObject(event);
			}
		}
		return result;
	}

}
