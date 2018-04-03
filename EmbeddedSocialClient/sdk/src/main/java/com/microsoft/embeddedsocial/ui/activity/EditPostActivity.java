/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.activity;

import com.microsoft.embeddedsocial.ui.activity.base.BaseActivity;
import com.microsoft.embeddedsocial.ui.fragment.EditPostFragment;

/**
 * Post editing.
 */
public class EditPostActivity extends BaseActivity {

	@Override
	protected void setupFragments() {
		EditPostFragment fragment = new EditPostFragment();
		fragment.setArguments(getIntent().getExtras());
		setActivityContent(fragment);
	}
}
