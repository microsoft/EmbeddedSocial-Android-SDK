/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.ui.activity;

import com.microsoft.socialplus.base.utils.ViewUtils;
import com.microsoft.socialplus.base.utils.thread.ThreadUtils;
import com.microsoft.socialplus.sdk.R;
import com.microsoft.socialplus.ui.activity.base.BaseActivity;
import com.microsoft.socialplus.ui.fragment.SignInFragment;

/**
 * Activity for sign-in.
 */
public class SignInActivity extends BaseActivity {

	public SignInActivity() {
		super(R.id.sp_navigationProfile);
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
