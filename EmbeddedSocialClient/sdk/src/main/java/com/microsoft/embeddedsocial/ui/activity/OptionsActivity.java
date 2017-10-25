/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.activity;

import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.ui.activity.base.BaseActivity;
import com.microsoft.embeddedsocial.ui.fragment.OptionsFragment;

/**
 * Shows settings.
 */
public class OptionsActivity extends BaseActivity {
	public static final String NAME = "Options";

	public OptionsActivity() {
		super(R.id.es_navigationOptions);
	}

	@Override
	protected void setupFragments() {
		setActivityContent(new OptionsFragment());
	}

	@Override
	protected boolean isAuthorizationRequired() {
		return false;
	}

	@Override
	protected String getName() {
		return NAME;
	}
}
