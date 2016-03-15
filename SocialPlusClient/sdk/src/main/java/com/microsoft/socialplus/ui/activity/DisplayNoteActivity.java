/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.microsoft.socialplus.base.utils.debug.DebugLog;
import com.microsoft.socialplus.service.IntentExtras;
import com.microsoft.socialplus.ui.activity.base.BaseActivity;
import com.microsoft.socialplus.ui.fragment.DisplayCommentFragment;
import com.microsoft.socialplus.ui.fragment.DisplayReplyFragment;

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
