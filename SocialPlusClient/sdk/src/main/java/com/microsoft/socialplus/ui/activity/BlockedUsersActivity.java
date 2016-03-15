/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.ui.activity;

import com.microsoft.socialplus.ui.activity.base.BaseActivity;
import com.microsoft.socialplus.ui.fragment.BlockedUsersFragment;

/**
 * Activity showing the list of blocked users.
 */
public class BlockedUsersActivity extends BaseActivity {

	@Override
	protected void setupFragments() {
		setActivityContent(new BlockedUsersFragment());
	}

	@Override
	protected boolean isAuthorizationRequired() {
		return true;
	}

}
