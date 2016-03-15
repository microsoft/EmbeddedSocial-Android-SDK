/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.ui.activity;

import com.microsoft.socialplus.ui.activity.base.BaseActivity;
import com.microsoft.socialplus.ui.fragment.AddPostFragment;

/**
 * Activity for adding a new post.
 */
public class AddPostActivity extends BaseActivity {

	@Override
	protected void setupFragments() {
		setActivityContent(AddPostFragment.newInstance());
	}

	@Override
	protected boolean isAuthorizationRequired() {
		return true;
	}
}
