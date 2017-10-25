/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.activity;

import android.os.Bundle;

import com.microsoft.embeddedsocial.ui.activity.base.BaseDiscussionActivity;
import com.microsoft.embeddedsocial.ui.fragment.ReplyFeedFragment;

public class CommentActivity extends BaseDiscussionActivity {

	@Override
	protected void setupFragments() {
		final ReplyFeedFragment feedFragment = new ReplyFeedFragment();
		feedFragment.setArguments(getIntent().getExtras());
		setActivityContent(feedFragment);
	}
}
