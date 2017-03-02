/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.activity.base;

import android.os.Bundle;

import com.microsoft.embeddedsocial.ui.fragment.FeedViewMenuFragment;

/**
 * Base class for activities showing topics feed.
 */
public abstract class BaseFeedActivity extends BaseActivity {

	protected BaseFeedActivity(int navigationItem) {
		super(navigationItem);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(new FeedViewMenuFragment(), FeedViewMenuFragment.TAG).commit();
		}
	}

}
