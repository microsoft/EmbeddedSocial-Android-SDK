/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.util;

import android.content.Context;
import android.content.Intent;

import com.microsoft.embeddedsocial.account.UserAccount;
import com.microsoft.embeddedsocial.server.model.view.UserCompactView;
import com.microsoft.embeddedsocial.ui.activity.MyProfileActivity;
import com.microsoft.embeddedsocial.autorest.models.FollowerStatus;
import com.microsoft.embeddedsocial.service.IntentExtras;
import com.microsoft.embeddedsocial.ui.activity.AnotherUserProfileActivity;

/**
 * Open user profile from any place.
 */
public final class ProfileOpenHelper {

	private ProfileOpenHelper() {
	}

	public static void openUserProfile(Context context, UserCompactView user) {
		String userHandle = user.getHandle();
		if (UserAccount.getInstance().isCurrentUser(userHandle)) {
			openCurrentUserProfile(context);
		} else {
			Intent intent = new Intent(context, AnotherUserProfileActivity.class);
			intent.putExtra(IntentExtras.USER_HANDLE, userHandle);
			boolean feedIsNotReadable = user.getFollowerStatus() == FollowerStatus.BLOCKED
				|| (user.isPrivate() && user.getFollowerStatus() != FollowerStatus.FOLLOW);
			intent.putExtra(IntentExtras.FEED_IS_NOT_READABLE, feedIsNotReadable);
			intent.putExtra(IntentExtras.NAME, user.getFullName());
			context.startActivity(intent);
		}
	}

	public static void openCurrentUserProfile(Context context) {
		Intent intent = new Intent(context, MyProfileActivity.class);
		context.startActivity(intent);
	}
}
