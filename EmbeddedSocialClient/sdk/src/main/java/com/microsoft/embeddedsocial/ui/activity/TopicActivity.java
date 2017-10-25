/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.activity;

import android.os.Bundle;

import com.microsoft.embeddedsocial.ui.activity.base.BaseDiscussionActivity;
import com.microsoft.embeddedsocial.ui.fragment.CommentFeedFragment;

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
}
