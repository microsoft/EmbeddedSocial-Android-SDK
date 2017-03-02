/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.activity.base;

import android.content.Intent;

import com.microsoft.embeddedsocial.service.IntentExtras;

/**
 * Base class for activities with discussion feed.
 */
public abstract class BaseDiscussionActivity extends BaseActivity {

	@Override
	protected Intent getRestartIntent() {
		Intent intent = getIntent();
		intent.removeExtra(IntentExtras.COMMENT_EXTRA);
		return intent;
	}

	@Override
	protected boolean isAuthorizationRequired() {
		return getIntent().getBooleanExtra(IntentExtras.JUMP_TO_EDIT, false);
	}
}
