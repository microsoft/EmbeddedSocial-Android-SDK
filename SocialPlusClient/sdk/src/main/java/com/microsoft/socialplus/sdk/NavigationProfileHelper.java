/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.sdk;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.microsoft.socialplus.account.UserAccount;
import com.microsoft.socialplus.base.event.EventBus;
import com.microsoft.socialplus.data.model.AccountData;
import com.microsoft.socialplus.event.click.NavigationItemClickedEvent;
import com.microsoft.socialplus.image.ImageViewContentLoader;
import com.microsoft.socialplus.image.UserPhotoLoader;
import com.microsoft.socialplus.ui.util.ContentUpdateHelper;
import com.microsoft.socialplus.ui.util.NavigationIntentUtils;

/**
 * Setup methods for navigation profile
 */
public class NavigationProfileHelper {
	private NavigationProfileHelper () {

	}

	public static ImageViewContentLoader setupNavigationProfile(Activity activity, ImageViewContentLoader photoLoader, ViewGroup navigationPanel, int activeItemId) {
		final boolean signedIn = UserAccount.getInstance().isSignedIn();
		final View profileItem = navigationPanel.findViewById(R.id.sp_navigationProfile);
		final ImageView profilePhoto = (ImageView) navigationPanel.findViewById(R.id.sp_photo);
		final TextView profileName = (TextView) navigationPanel.findViewById(R.id.sp_fullName);

		if (photoLoader != null) {
			photoLoader.cancel();
		}

		if (!signedIn) {
			profilePhoto.setImageResource(R.drawable.sp_user_no_pic_white);
			profileName.setText(R.string.sp_sign_in);
		} else {
			AccountData accountData = UserAccount.getInstance().getAccountDetails();
			photoLoader = new UserPhotoLoader(profilePhoto, R.drawable.sp_user_no_pic_white);
			ContentUpdateHelper.setProfileImage(
					photoLoader,
					accountData.getUserPhotoUrl(),
				R.drawable.sp_user_no_pic_white);
			profileName.setText(accountData.getFullName());
		}
		if (activeItemId == R.id.sp_navigationProfile) {
			profileItem.setBackgroundColor(Color.BLACK);
		} else {
			NavigationIntentUtils navigation = new NavigationIntentUtils(activity);
			if (signedIn) {
				profileItem.setOnClickListener(v -> {
					EventBus.post(new NavigationItemClickedEvent());
					navigation.gotoProfile();
				});
			} else {
				profileItem.setOnClickListener(v -> {
					EventBus.post(new NavigationItemClickedEvent());
					navigation.gotoSignIn();
				});
			}
		}

		return photoLoader;
	}
}
