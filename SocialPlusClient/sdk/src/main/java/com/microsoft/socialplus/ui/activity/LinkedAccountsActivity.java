/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.ui.activity;

import com.microsoft.socialplus.ui.activity.base.BaseActivity;
import com.microsoft.socialplus.ui.fragment.LinkedAccountsFragment;

/**
 * Activity showing the list of linked accounts.
 */
public class LinkedAccountsActivity extends BaseActivity {

	@Override
	protected void setupFragments() {
		setActivityContent(new LinkedAccountsFragment());
	}

	@Override
	protected boolean isAuthorizationRequired() {
		return true;
	}

}
