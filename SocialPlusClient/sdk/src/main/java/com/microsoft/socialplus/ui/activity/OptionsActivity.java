/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.ui.activity;

import android.os.Bundle;

import com.microsoft.socialplus.sdk.R;
import com.microsoft.socialplus.ui.activity.base.BaseActivity;
import com.microsoft.socialplus.ui.fragment.OptionsFragment;

/**
 * Shows settings.
 */
public class OptionsActivity extends BaseActivity {
	public OptionsActivity() {
		super(R.id.sp_navigationOptions);
	}

	@Override
	protected void setupFragments() {
		setActivityContent(new OptionsFragment());
	}

	@Override
	protected boolean isAuthorizationRequired() {
		return true;
	}
}
