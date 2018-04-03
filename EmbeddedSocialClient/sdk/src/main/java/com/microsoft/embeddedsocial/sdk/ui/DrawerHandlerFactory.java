/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.sdk.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.sdk.NavigationMenuDescription;

/**
 * Generate DrawerHandler for specific mode
 */
public class DrawerHandlerFactory {

	private DrawerHandlerFactory() {  }

	public static DrawerHandler createHandler(@NonNull AppCompatActivity activity, Bundle args) {
		DrawerHandler drawerHandler = null;
		NavigationMenuDescription navigationMenu = GlobalObjectRegistry.getObject(NavigationMenuDescription.class);
		if (navigationMenu != null) {
			final Fragment hostingAppMenuFragment = navigationMenu.getDrawerFactory().createMenuFragment();
			hostingAppMenuFragment.setArguments(args);

			switch (navigationMenu.getDisplayMode()) {
				case TABS:
					drawerHandler = new TabbedDrawerHandler(activity, hostingAppMenuFragment, navigationMenu.getTabTitle());
					break;
				case SWITCHER:
					drawerHandler = new SwitcherDrawerHandler(activity, hostingAppMenuFragment, navigationMenu.getTabTitle());
					break;
			}
		} else {
			drawerHandler = new SingleDrawerHandler(activity);
		}

		return drawerHandler;
	}
}
