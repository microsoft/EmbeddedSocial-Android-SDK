/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.activity;

import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.ui.activity.base.BaseActivity;
import com.microsoft.embeddedsocial.ui.fragment.PopularFeedFragment;

/**
 * Activity showing popular feeds.
 */
public class PopularActivity extends BaseActivity {
	public static final String NAME = "Popular";

	public PopularActivity() {
		super(R.id.es_navigationPopular);
	}

	@Override
	protected void setupFragments() {
		setActivityContent(new PopularFeedFragment());
		super.setupFragments();
	}

	@Override
	protected String getName() {
		return NAME;
	}
}
