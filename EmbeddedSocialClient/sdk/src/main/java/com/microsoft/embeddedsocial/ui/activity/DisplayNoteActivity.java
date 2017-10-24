/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.ui.activity.base.BaseActivity;
import com.microsoft.embeddedsocial.ui.fragment.DisplayReplyFragment;
import com.microsoft.embeddedsocial.service.IntentExtras;
import com.microsoft.embeddedsocial.ui.fragment.DisplayCommentFragment;

/**
 * Activity to display a single comment or reply.
 */
public class DisplayNoteActivity extends BaseActivity {

	@Override
	protected void setupFragments() {
		Bundle extras = getIntent().getExtras();
		if (extras == null ||
			(!extras.containsKey(IntentExtras.COMMENT_HANDLE) && !extras.containsKey(IntentExtras.REPLY_HANDLE))) {
			DebugLog.e(
				String.format("DisplayNoteActivity should contains one of the extras [\"%s\", \"%s\"] to right work.",
					IntentExtras.COMMENT_HANDLE, IntentExtras.REPLY_HANDLE));
			return;
		}

		final Fragment noteFragment = (extras.containsKey(IntentExtras.COMMENT_HANDLE)) ?
			new DisplayCommentFragment() : new DisplayReplyFragment();
		noteFragment.setArguments(getIntent().getExtras());
		setActivityContent(noteFragment);
	}
}
