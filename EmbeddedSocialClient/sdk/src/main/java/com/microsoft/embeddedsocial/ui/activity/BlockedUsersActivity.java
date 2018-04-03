/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.activity;

import com.microsoft.embeddedsocial.ui.activity.base.BaseActivity;
import com.microsoft.embeddedsocial.ui.fragment.BlockedUsersFragment;

import android.os.Bundle;

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
