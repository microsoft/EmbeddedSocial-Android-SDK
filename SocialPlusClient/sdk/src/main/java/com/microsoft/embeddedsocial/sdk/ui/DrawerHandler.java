/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.sdk.ui;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

/**
 * Base class for menu's different views.
 */
public abstract class DrawerHandler {
	protected static final String HOSTING_APP_TAG = "HOSTING";
	protected static final String SOCIAL_TAG = "SOCIAL";

	protected final AppCompatActivity activity;
	protected final Fragment hostingAppMenuFragment;
	protected final CharSequence hostingAppMenuTitle;
	protected Fragment embeddedSocialMenuFragment;
	protected ViewGroup drawerContainer;
	protected int embeddedSocialMenuActiveItemId;
	protected DisplayMenu displayMenu;

	DrawerHandler(@NonNull AppCompatActivity activity, Fragment hostingAppMenuFragment, CharSequence hostingAppMenuTitle) {
		this.hostingAppMenuFragment = hostingAppMenuFragment;
		this.activity = activity;
		this.hostingAppMenuTitle = hostingAppMenuTitle;
	}

	public void inflate(ViewGroup drawerContainer, int embeddedSocialMenuActiveItemId) {
		this.drawerContainer = drawerContainer;
		this.embeddedSocialMenuActiveItemId = embeddedSocialMenuActiveItemId;
	}

	public void setDisplayMenu(DisplayMenu displayMenu) {
		this.displayMenu = displayMenu;
	}

	public DisplayMenu getDisplayMenu() {
		return displayMenu;
	}

	CharSequence getHostingAppMenuTitle() {
		return hostingAppMenuTitle;
	}

	public void onResumeSetup() {
		// Not used by default
	}

	public enum DisplayMenu {
		HOST_MENU,
		SOCIAL_MENU
	}
}
