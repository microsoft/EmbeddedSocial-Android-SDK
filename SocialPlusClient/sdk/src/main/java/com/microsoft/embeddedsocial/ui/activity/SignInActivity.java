/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.activity;

import com.microsoft.embeddedsocial.base.utils.ViewUtils;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.ui.activity.base.BaseActivity;
import com.microsoft.embeddedsocial.ui.fragment.SignInFragment;
import com.microsoft.embeddedsocial.base.utils.thread.ThreadUtils;

/**
 * Activity for sign-in.
 */
public class SignInActivity extends BaseActivity {

	public SignInActivity() {
		super(R.id.es_navigationProfile);
	}

	@Override
	protected void setupFragments() {
		setActivityContent(new SignInFragment());
	}

	@Override
	protected boolean isAuthorizationRequired() {
		return false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		// XXX: keyboard is closed in a handler because of bugs on HTC devices
		ThreadUtils.getMainThreadHandler().post(() -> ViewUtils.hideKeyboard(this));
	}
}
