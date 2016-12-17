/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.activity;

import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.ui.activity.base.BaseFeedActivity;
import com.microsoft.embeddedsocial.ui.fragment.PinsFragment;

/**
 * Shows pinned topics.
 */
public class PinsActivity extends BaseFeedActivity {

	public PinsActivity() {
		super(R.id.sp_navigationPins);
	}

	@Override
	protected void setupFragments() {
		setActivityContent(new PinsFragment());
	}

	@Override
	protected boolean isAuthorizationRequired() {
		return true;
	}
}