/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.activity;

import com.microsoft.embeddedsocial.ui.activity.base.BaseActivity;
import com.microsoft.embeddedsocial.ui.fragment.FollowRequestsFragment;

/**
 * Shows all follow requests.
 */
public class FollowRequestsActivity extends BaseActivity {

	@Override
	protected void setupFragments() {
		setActivityContent(new FollowRequestsFragment());
	}

	@Override
	protected boolean isAuthorizationRequired() {
		return true;
	}
}
