/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;

import com.microsoft.autorest.models.IdentityProvider;
import com.microsoft.socialplus.base.utils.EnumUtils;
import com.microsoft.socialplus.sdk.R;
import com.microsoft.socialplus.service.IntentExtras;
import com.microsoft.socialplus.ui.activity.base.BaseActivity;
import com.microsoft.socialplus.ui.fragment.FriendlistFragment;

/**
 * Search users from social networks.
 */
public class FriendlistActivity extends BaseActivity {

	public static final String EXTRA_ACTIVITY_RESULT_REDIRECT = "activityResultRedirect";

	private IdentityProvider identityProvider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		identityProvider = EnumUtils.getValue(getIntent(), IntentExtras.IDENTITY_PROVIDER, IdentityProvider.class);
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Fragment contentFragment = getSupportFragmentManager().findFragmentById(R.id.sp_content);
		if (contentFragment != null && contentFragment instanceof FriendlistFragment) {
			if (data == null) {
				data = new Intent();
			}
			data.putExtra(EXTRA_ACTIVITY_RESULT_REDIRECT, true);
			contentFragment.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	protected void setupActionBar(ActionBar actionBar) {
		super.setupActionBar(actionBar);
		if (identityProvider != null) {
			String title;
			switch (identityProvider) {
				case MICROSOFT:
					title = getString(R.string.sp_button_find_friends_microsoft);
					break;
				case GOOGLE:
					title = getString(R.string.sp_button_find_friends_google_plus);
					break;
				case FACEBOOK:
					title = getString(R.string.sp_button_find_friends_facebook);
					break;
				default:
					title = "";
			}
			actionBar.setTitle(title);
		}
	}

	@Override
	protected void setupFragments() {
		if (identityProvider == null) {
			finish();
		} else {
			setActivityContent(FriendlistFragment.create(identityProvider));
		}
	}
}
