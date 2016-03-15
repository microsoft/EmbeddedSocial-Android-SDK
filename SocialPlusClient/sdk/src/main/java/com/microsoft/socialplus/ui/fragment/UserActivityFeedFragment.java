/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.ui.fragment;

import com.microsoft.socialplus.fetcher.FetchersFactory;
import com.microsoft.socialplus.fetcher.base.FetchableAdapter;
import com.microsoft.socialplus.server.model.view.ActivityView;
import com.microsoft.socialplus.ui.adapter.FetchableListAdapter;
import com.microsoft.socialplus.ui.adapter.renderer.UserRecentActivityRenderer;
import com.microsoft.socialplus.ui.fragment.base.BaseActivityFeedFragment;

/**
 * Shows recent activity.
 */
public class UserActivityFeedFragment extends BaseActivityFeedFragment {

	@Override
	protected FetchableAdapter<ActivityView, ?> createInitialAdapter() {
		return new FetchableListAdapter<>(
			FetchersFactory.createNotificationFeedFetcher(),
			new UserRecentActivityRenderer()
		);
	}

}
