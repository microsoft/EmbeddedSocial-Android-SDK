/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;

import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.ui.activity.base.BaseActivity;
import com.microsoft.embeddedsocial.ui.fragment.FriendlistFragment;
import com.microsoft.embeddedsocial.autorest.models.IdentityProvider;
import com.microsoft.embeddedsocial.base.utils.EnumUtils;
import com.microsoft.embeddedsocial.service.IntentExtras;

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
		Fragment contentFragment = getSupportFragmentManager().findFragmentById(R.id.es_content);
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
					title = getString(R.string.es_button_find_friends_microsoft);
					break;
				case GOOGLE:
					title = getString(R.string.es_button_find_friends_google_plus);
					break;
				case FACEBOOK:
					title = getString(R.string.es_button_find_friends_facebook);
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
