/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.activity;

import android.os.Bundle;

import com.microsoft.embeddedsocial.ui.fragment.LinkedAccountsFragment;
import com.microsoft.embeddedsocial.ui.activity.base.BaseActivity;

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
