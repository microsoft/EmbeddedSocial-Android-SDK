/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.ui.activity;

import android.os.Bundle;

import com.microsoft.socialplus.ui.activity.base.BaseDiscussionActivity;
import com.microsoft.socialplus.ui.fragment.CommentFeedFragment;

/**
 * Activity to display a single topic.
 */
public class TopicActivity extends BaseDiscussionActivity {

	@Override
	protected void setupFragments() {
		final CommentFeedFragment feedFragment = new CommentFeedFragment();
		feedFragment.setArguments(getIntent().getExtras());
		setActivityContent(feedFragment);
	}

	@Override
	protected void initView(Bundle savedInstanceState) {
		super.initView(savedInstanceState);
		setNonNavDrawerToolbar();
	}
}
