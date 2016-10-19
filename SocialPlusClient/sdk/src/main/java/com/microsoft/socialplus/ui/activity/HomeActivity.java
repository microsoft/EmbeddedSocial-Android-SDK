/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.ui.activity;


import android.content.Intent;
import android.support.v7.app.ActionBar;

import com.microsoft.socialplus.sdk.R;
import com.microsoft.socialplus.ui.activity.base.BaseFeedActivity;
import com.microsoft.socialplus.ui.fragment.FollowingFeedFragment;

/**
 * Activity to show feeds and profile.
 */
public class HomeActivity extends BaseFeedActivity {

	public HomeActivity() {
		super(R.id.sp_navigationHome);
	}

	@Override
	protected void setupFragments() {
		setActivityContent(new FollowingFeedFragment());
	}

	@Override
	protected void setupActionBar(ActionBar actionBar) {
		super.setupActionBar(actionBar);
		actionBar.setTitle(R.string.sp_navigation_home);
	}

	@Override
	protected void onUnauthorizedAccess() {
		startActivity(new Intent(this, PopularActivity.class));
		finish();
	}

	@Override
	protected boolean isAuthorizationRequired() {
		return true;
	}
}
