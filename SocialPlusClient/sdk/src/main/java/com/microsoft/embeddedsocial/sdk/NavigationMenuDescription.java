/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.sdk;

import com.microsoft.embeddedsocial.sdk.ui.DrawerDisplayMode;

/**
 * Stores hosting app navigation menu data.
 */
public class NavigationMenuDescription {

	private final INavigationDrawerFactory drawerFactory;
	private final DrawerDisplayMode displayMode;
	private final CharSequence tabTitle;

	public NavigationMenuDescription(INavigationDrawerFactory drawerFactory,
	                                 DrawerDisplayMode displayMode, CharSequence tabTitle) {

		this.drawerFactory = drawerFactory;
		this.displayMode = displayMode;
		this.tabTitle = tabTitle;
	}

	public INavigationDrawerFactory getDrawerFactory() {
		return drawerFactory;
	}

	public DrawerDisplayMode getDisplayMode() {
		return displayMode;
	}

	public CharSequence getTabTitle() {
		return tabTitle;
	}
}
