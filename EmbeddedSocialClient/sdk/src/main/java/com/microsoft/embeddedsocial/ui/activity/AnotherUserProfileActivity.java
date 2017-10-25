/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;

import com.microsoft.embeddedsocial.account.UserAccount;
import com.microsoft.embeddedsocial.ui.activity.base.BaseProfileActivity;
import com.microsoft.embeddedsocial.service.IntentExtras;

/**
 * Shows another user's profile.
 */
public class AnotherUserProfileActivity extends BaseProfileActivity {

	private String userHandle;

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		userHandle = getIntent().getStringExtra(IntentExtras.USER_HANDLE);
	}

	@Override
	protected void setupActionBar(ActionBar actionBar) {
		super.setupActionBar(actionBar);
		String name = getIntent().getStringExtra(IntentExtras.NAME);
		actionBar.setTitle(name);
	}

	@Override
	protected void initExtraVariables() {
		Intent intent = getIntent();
		setUserHandle(intent.getStringExtra(IntentExtras.USER_HANDLE));
		setUserName(intent.getStringExtra(IntentExtras.NAME));
		setIsCurrentUser(false);
		setFeedIsReadable(!intent.getBooleanExtra(IntentExtras.FEED_IS_NOT_READABLE, false));
	}

	@Override
	protected Intent getRestartIntent() {
		if (UserAccount.getInstance().isCurrentUser(userHandle)) {
			return new Intent(this, MyProfileActivity.class);
		} else {
			return super.getRestartIntent();
		}
	}
}