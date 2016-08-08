/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.ui.activity;

import android.os.Bundle;
import android.view.View;

import com.microsoft.socialplus.sdk.R;
import com.microsoft.socialplus.ui.activity.base.BaseActivity;
import com.microsoft.socialplus.ui.fragment.TextFragment;

/**
 * Shows the privacy policy.
 */
public class PrivacyPolicyActivity extends BaseActivity {

	@Override
	protected void setupFragments() {
		setActivityContent(TextFragment.create(R.string.sp_privacy_policy_text));
	}

	@Override
	protected void initView(Bundle savedInstanceState) {
		super.initView(savedInstanceState);
		setNonNavDrawerToolbar();
	}
}
