/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.activity;

import android.os.Bundle;

import com.microsoft.embeddedsocial.ui.fragment.LikesFragment;
import com.microsoft.embeddedsocial.ui.activity.base.BaseActivity;

/**
 * Screen with users who liked a topic.
 */
public class LikesActivity extends BaseActivity {

	@Override
	protected void setupFragments() {
		setActivityContent(new LikesFragment());
	}
}
