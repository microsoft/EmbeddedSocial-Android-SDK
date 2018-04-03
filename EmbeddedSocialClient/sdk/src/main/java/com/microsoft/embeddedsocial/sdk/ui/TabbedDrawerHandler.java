/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.sdk.ui;

import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.ui.fragment.NavigationFragment;

public class TabbedDrawerHandler extends DrawerHandler implements TabLayout.OnTabSelectedListener {
	private TabLayout.Tab hostingAppMenuTab;
	private TabLayout.Tab socialMenuTab;

	public TabbedDrawerHandler(@NonNull AppCompatActivity activity, @NonNull Fragment hostingAppMenuFragment, CharSequence hostingAppMenuTitle) {
		super(activity, hostingAppMenuFragment, hostingAppMenuTitle);
	}

	@Override
	public void inflate(ViewGroup drawerContainer, int embeddedSocialMenuActiveItemId) {
		super.inflate(drawerContainer, embeddedSocialMenuActiveItemId);
		drawerContainer.removeAllViews();
		drawerContainer.addView(
			activity.getLayoutInflater().inflate(R.layout.es_sdk_fragment_tabs, drawerContainer, false));

		TabLayout drawerMenu = (TabLayout) activity.findViewById(R.id.es_tabs);

		hostingAppMenuTab = drawerMenu.newTab()
			.setTag(HOSTING_APP_TAG)
			.setText(this.hostingAppMenuTitle);

		socialMenuTab = drawerMenu.newTab()
			.setTag(SOCIAL_TAG)
			.setCustomView(R.layout.es_tab_icon);

		drawerMenu.setOnTabSelectedListener(this);
		drawerMenu.addTab(hostingAppMenuTab, true);
		drawerMenu.addTab(socialMenuTab);

		embeddedSocialMenuFragment = NavigationFragment.create(embeddedSocialMenuActiveItemId);
	}

	@Override
	public void setDisplayMenu(DisplayMenu displayMenu) {
		super.setDisplayMenu(displayMenu);
		switch (displayMenu) {
			case HOST_MENU:
				hostingAppMenuTab.select();
				break;
			case SOCIAL_MENU:
				socialMenuTab.select();
				break;
		}
	}

	private void selectHostingAppMenuFragment() {
		displayMenu = DisplayMenu.HOST_MENU;
		FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.es_tab_container, hostingAppMenuFragment, HOSTING_APP_TAG);
		fragmentTransaction.commit();
	}

	private void selectEmbeddedSocialMenuFragment() {
		displayMenu = DisplayMenu.SOCIAL_MENU;
		FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.es_tab_container, embeddedSocialMenuFragment, SOCIAL_TAG);
		fragmentTransaction.commit();
	}

	@Override
	public void onTabSelected(TabLayout.Tab tab) {
		if (HOSTING_APP_TAG.equals(tab.getTag())) {
			selectHostingAppMenuFragment();
		} else if (SOCIAL_TAG.equals(tab.getTag())) {
			selectEmbeddedSocialMenuFragment();
		}
	}

	@Override
	public void onTabUnselected(TabLayout.Tab tab) {
		// not used
	}

	@Override
	public void onTabReselected(TabLayout.Tab tab) {
		// not used
	}
}
