/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.activity;

import android.os.Bundle;

import com.microsoft.embeddedsocial.ui.activity.base.BaseActivity;
import com.microsoft.embeddedsocial.ui.fragment.FollowingFragment;

/**
 * Activity showing people you are following.
 */
public class FollowingActivity extends BaseActivity {

	@Override
	protected void setupFragments() {
		setActivityContent(new FollowingFragment());
	}
}
