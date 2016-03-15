/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.ui.activity;

import android.support.v4.view.PagerAdapter;

import com.microsoft.socialplus.sdk.R;
import com.microsoft.socialplus.ui.activity.base.BaseTabsActivity;
import com.microsoft.socialplus.ui.fragment.FollowingActivityFeedFragment;
import com.microsoft.socialplus.ui.fragment.UserActivityFeedFragment;
import com.microsoft.socialplus.ui.util.SimplePagerAdapter;

/**
 * Shows recent activity.
 */
public class RecentActivityActivity extends BaseTabsActivity {

	public RecentActivityActivity() {
		super(R.id.sp_navigationActivity);
	}

	@Override
	protected PagerAdapter createPagerAdapter() {
		return new SimplePagerAdapter(this, getSupportFragmentManager(),
			new SimplePagerAdapter.Page(R.string.sp_activity_feed_user, UserActivityFeedFragment::new),
			new SimplePagerAdapter.Page(R.string.sp_activity_feed_following, FollowingActivityFeedFragment::new)
		);
	}

	@Override
	protected boolean isAuthorizationRequired() {
		return true;
	}
}
