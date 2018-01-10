/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.activity.base;

import com.microsoft.embeddedsocial.ui.fragment.FeedViewMenuListenerFragment;

/**
 * Base class for activities showing a profile.
 */
public abstract class BaseProfileActivity extends BaseActivity {
	protected BaseProfileActivity() {
	}

	protected BaseProfileActivity(int activeNavigationItemId) {
		super(activeNavigationItemId);
	}

	@Override
	protected void setupFragments() {
		super.setupFragments();
		getSupportFragmentManager().beginTransaction().add(new FeedViewMenuListenerFragment(), FeedViewMenuListenerFragment.TAG).commit();
	}
}
