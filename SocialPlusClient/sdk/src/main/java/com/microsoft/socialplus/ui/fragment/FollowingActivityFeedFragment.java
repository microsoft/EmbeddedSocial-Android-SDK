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
import com.microsoft.socialplus.ui.adapter.renderer.FollowingRecentActivityRenderer;
import com.microsoft.socialplus.ui.fragment.base.BaseActivityFeedFragment;

/**
 * Fragment showing the activity feed of following users.
 */
public class FollowingActivityFeedFragment extends BaseActivityFeedFragment {

	@Override
	protected FetchableAdapter<ActivityView, ?> createInitialAdapter() {
		return new FetchableListAdapter<>(
			FetchersFactory.createFollowingActivityFeedFetcher(),
			new FollowingRecentActivityRenderer()
		);
	}

}
