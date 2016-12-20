/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.sdk.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.ui.fragment.NavigationFragment;
import com.microsoft.embeddedsocial.ui.theme.ThemeGroup;
import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.image.ImageViewContentLoader;
import com.microsoft.embeddedsocial.sdk.NavigationProfileHelper;
import com.microsoft.embeddedsocial.sdk.Options;
import com.microsoft.embeddedsocial.ui.theme.Theme;
import com.microsoft.embeddedsocial.ui.view.NavigationItemView;

public class SwitcherDrawerHandler extends DrawerHandler {
	private final LayoutInflater themedInflater;

	@SuppressWarnings("FieldCanBeLocal")
	private ImageViewContentLoader photoLoader;

	public SwitcherDrawerHandler(@NonNull AppCompatActivity activity, @NonNull Fragment hostingAppMenuFragment, CharSequence hostingAppMenuTitle) {
		super(activity, hostingAppMenuFragment, hostingAppMenuTitle);
		ThemeGroup themeGroup = GlobalObjectRegistry.getObject(Options.class).getThemeGroup();
		final Context context = new ContextThemeWrapper(activity, themeGroup.getThemeResId(Theme.REGULAR));
		themedInflater = activity.getLayoutInflater().cloneInContext(context);
	}

	@Override
	public void inflate(ViewGroup drawerContainer, int embeddedSocialMenuActiveItemId) {
		super.inflate(drawerContainer, embeddedSocialMenuActiveItemId);
		embeddedSocialMenuFragment = NavigationFragment.create(embeddedSocialMenuActiveItemId, false);
		setDisplayMenu(DisplayMenu.HOST_MENU);
	}

	@Override
	public void onResumeSetup() {
		super.onResumeSetup();
		setupNavigationProfile();
	}

	@Override
	public void setDisplayMenu(DisplayMenu displayMenu) {
		super.setDisplayMenu(displayMenu);
		switch (this.displayMenu) {
			case HOST_MENU:
				displaySwitcherHostingAppMenuFragment();
				break;
			case SOCIAL_MENU:
				displaySwitcherEmbeddedSocialMenuFragment();
				break;
		}
		setupNavigationProfile();
	}

	private void setupNavigationProfile() {
		photoLoader = NavigationProfileHelper.setupNavigationProfile(
				activity, photoLoader, drawerContainer, embeddedSocialMenuActiveItemId);
	}

	private void displaySwitcherHostingAppMenuFragment() {
		removeFragment(embeddedSocialMenuFragment);

		drawerContainer.removeAllViews();
		drawerContainer.addView(themedInflater.inflate(R.layout.es_sdk_fragment_switcher, drawerContainer, false));

		final NavigationItemView socialItemView = (NavigationItemView) activity.findViewById(R.id.es_navigationFirstMenu);
		socialItemView.setName(activity.getString(R.string.es_lib_title));
		socialItemView.hideIcon();
		socialItemView.setOnClickListener(v -> setDisplayMenu(DisplayMenu.SOCIAL_MENU));

		final NavigationItemView hostAppItemView = (NavigationItemView) activity.findViewById(R.id.es_navigationSecondMenu);
		hostAppItemView.setName(hostingAppMenuTitle);
		hostAppItemView.hideIcon();

		FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.es_menu_container, hostingAppMenuFragment, HOSTING_APP_TAG);
		fragmentTransaction.commit();
	}

	private void displaySwitcherEmbeddedSocialMenuFragment() {
		removeFragment(hostingAppMenuFragment);

		drawerContainer.removeAllViews();
		drawerContainer.addView(themedInflater.inflate(R.layout.es_sdk_fragment_switcher, drawerContainer, false));

		final NavigationItemView hostAppItemView = (NavigationItemView) activity.findViewById(R.id.es_navigationFirstMenu);
		hostAppItemView.setName(hostingAppMenuTitle);
		hostAppItemView.hideIcon();
		hostAppItemView.setOnClickListener(v -> setDisplayMenu(DisplayMenu.HOST_MENU));

		final NavigationItemView socialItemView = (NavigationItemView) activity.findViewById(R.id.es_navigationSecondMenu);
		socialItemView.setName(activity.getString(R.string.es_lib_title));
		socialItemView.hideIcon();

		FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.es_menu_container, embeddedSocialMenuFragment, SOCIAL_TAG);
		fragmentTransaction.commit();
	}

	private void removeFragment(Fragment fragment) {
		FrameLayout menuContainer = (FrameLayout) activity.findViewById(R.id.es_menu_container);
		if (menuContainer != null) {
			FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
			fragmentTransaction.remove(fragment);
			fragmentTransaction.commit();
		}
	}
}
