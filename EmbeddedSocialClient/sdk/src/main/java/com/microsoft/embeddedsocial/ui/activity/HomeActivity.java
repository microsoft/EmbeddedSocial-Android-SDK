/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.activity;


import android.content.Intent;
import android.support.v7.app.ActionBar;

import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.ui.activity.base.BaseFeedActivity;
import com.microsoft.embeddedsocial.ui.fragment.FollowingFeedFragment;

/**
 * Activity to show feeds and profile.
 */
public class HomeActivity extends BaseFeedActivity {
	public static final String NAME = "Home";

	public HomeActivity() {
		super(R.id.es_navigationHome);
	}

	@Override
	protected void setupFragments() {
		setActivityContent(new FollowingFeedFragment());
	}

	@Override
	protected void setupActionBar(ActionBar actionBar) {
		super.setupActionBar(actionBar);
		actionBar.setTitle(R.string.es_navigation_home);
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

	@Override
	protected String getName() {
		return NAME;
	}
}
