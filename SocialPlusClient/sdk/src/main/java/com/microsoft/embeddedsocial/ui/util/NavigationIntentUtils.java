/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.util;

import android.app.Activity;
import android.content.Intent;

import com.microsoft.embeddedsocial.sdk.ui.EmbeddedSocialNavigationActivity;
import com.microsoft.embeddedsocial.ui.activity.GateActivity;
import com.microsoft.embeddedsocial.ui.activity.HomeActivity;
import com.microsoft.embeddedsocial.ui.activity.MyProfileActivity;
import com.microsoft.embeddedsocial.ui.activity.PinsActivity;
import com.microsoft.embeddedsocial.ui.activity.PopularActivity;
import com.microsoft.embeddedsocial.ui.activity.RecentActivityActivity;
import com.microsoft.embeddedsocial.ui.activity.SignInActivity;
import com.microsoft.embeddedsocial.ui.activity.base.BaseActivity;
import com.microsoft.embeddedsocial.ui.activity.OptionsActivity;
import com.microsoft.embeddedsocial.ui.activity.SearchActivity;

/**
 * Contains methods for navigation in Embedded Social sdk.
 */
public class NavigationIntentUtils {

	private final Activity activity;
	private final boolean isOutsideSdk;

	public NavigationIntentUtils(Activity activity) {
		this.activity = activity;
		isOutsideSdk = !(activity instanceof BaseActivity);
	}

	/**
	 * Launches the Home activity.
	 */
	public void gotoHome() {
		launchActivity(HomeActivity.class);
	}

	/**
	 * Launches the Sign-in activity.
	 */
	public void gotoSignIn() {
		launchActivity(SignInActivity.class);
	}

	/**
	 * Launches the user's profile activity.
	 */
	public void gotoProfile() {
		launchGateActivityIfNeeded();
		Intent intent = new Intent(activity, MyProfileActivity.class);
		setupHostActivity(intent);
		activity.startActivity(intent);

	}

	/**
	 * Launches the Search activity.
	 */
	public void gotoSearch() {
		launchActivity(SearchActivity.class);
	}

	/**
	 * Launches the Popular activity.
	 */
	public void gotoPopular() {
		launchActivity(PopularActivity.class);
	}

	/**
	 * Launches the My Pins activity.
	 */
	public void gotoPins() {
		launchActivity(PinsActivity.class);
	}

	/**
	 * Launches the Activity Feed activity.
	 */
	public void gotoActivityFeed() {
		launchActivity(RecentActivityActivity.class);
	}

	/**
	 * Launches the Options activity.
	 */
	public void gotoOptions() {
		launchActivity(OptionsActivity.class);
	}

	/**
	 * Closes all Embedded Social sdk activities.
	 */
	public void closeSdkActivities() {
		Intent intent = new Intent(activity, GateActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		if (!isOutsideSdk) {
			activity.finish();
		}
		activity.startActivity(intent);
	}

	private void launchActivity(Class<? extends Activity> activityClass) {
		launchGateActivityIfNeeded();
		Intent intent = new Intent(activity, activityClass);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		setupHostActivity(intent);
		activity.startActivity(intent);
	}

	private void launchGateActivityIfNeeded() {
		if (isOutsideSdk) {
			activity.startActivity(new Intent(activity, GateActivity.class));
		}
	}

	private void setupHostActivity(Intent intent) {
		if (activity instanceof EmbeddedSocialNavigationActivity) {
			intent.putExtra(
					BaseActivity.HOST_MENU_BUNDLE_EXTRA,
					((EmbeddedSocialNavigationActivity) activity).getHostAppExtras());
		}
	}
}
