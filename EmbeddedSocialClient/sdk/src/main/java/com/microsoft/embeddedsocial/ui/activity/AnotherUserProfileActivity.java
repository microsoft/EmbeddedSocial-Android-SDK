/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.activity;

import com.microsoft.embeddedsocial.account.UserAccount;
import com.microsoft.embeddedsocial.service.IntentExtras;
import com.microsoft.embeddedsocial.ui.activity.base.BaseProfileActivity;
import com.microsoft.embeddedsocial.ui.fragment.AnotherUserProfileFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;

/**
 * Shows another user's profile.
 */
public class AnotherUserProfileActivity extends BaseProfileActivity {
	@Override
	protected void setupFragments() {
		setActivityContent(new AnotherUserProfileFragment());
		super.setupFragments();
	}

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
	protected Intent getRestartIntent() {
		if (UserAccount.getInstance().isCurrentUser(userHandle)) {
			return new Intent(this, MyProfileActivity.class);
		} else {
			return super.getRestartIntent();
		}
	}
}