/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;

import com.microsoft.socialplus.account.UserAccount;
import com.microsoft.socialplus.sdk.R;
import com.microsoft.socialplus.ui.activity.base.BaseProfileActivity;

/**
 * Activity shoving current user's feeds.
 */
public class MyProfileActivity extends BaseProfileActivity {

	public MyProfileActivity() {
		super(R.id.sp_navigationProfile);
	}

	@Override
	protected void initExtraVariables() {
		setUserHandle(UserAccount.getInstance().getUserHandle());
		setIsCurrentUser(true);
		setFeedIsReadable(true);
	}

	@Override
	protected void setupActionBar(ActionBar actionBar) {
		super.setupActionBar(actionBar);
		actionBar.setTitle(UserAccount.getInstance().getAccountDetails().getFullName());
	}

	@Override
	protected boolean isAuthorizationRequired() {
		return true;
	}
}
