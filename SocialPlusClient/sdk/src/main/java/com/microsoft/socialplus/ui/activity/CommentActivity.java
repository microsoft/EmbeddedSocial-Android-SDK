/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.ui.activity;

import android.os.Bundle;

import com.microsoft.socialplus.ui.activity.base.BaseDiscussionActivity;
import com.microsoft.socialplus.ui.fragment.ReplyFeedFragment;

public class CommentActivity extends BaseDiscussionActivity {

	@Override
	protected void setupFragments() {
		final ReplyFeedFragment feedFragment = new ReplyFeedFragment();
		feedFragment.setArguments(getIntent().getExtras());
		setActivityContent(feedFragment);
	}

	@Override
	protected void initView(Bundle savedInstanceState) {
		super.initView(savedInstanceState);
		setNonNavDrawerToolbar();
	}
}
