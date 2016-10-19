/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.ui.activity;

import android.os.Bundle;

import com.microsoft.socialplus.ui.activity.base.BaseActivity;
import com.microsoft.socialplus.ui.fragment.LikesFragment;

/**
 * Screen with users who liked a topic.
 */
public class LikesActivity extends BaseActivity {

	@Override
	protected void setupFragments() {
		setActivityContent(new LikesFragment());
	}

	@Override
	protected void initView(Bundle savedInstanceState) {
		super.initView(savedInstanceState);
		setNonNavDrawerToolbar();
	}
}
