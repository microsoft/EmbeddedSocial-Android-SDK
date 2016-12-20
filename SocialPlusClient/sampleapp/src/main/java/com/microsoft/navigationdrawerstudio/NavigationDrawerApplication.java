/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.navigationdrawerstudio;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.microsoft.embeddedsocial.sdk.EmbeddedSocial;
import com.microsoft.embeddedsocial.sdk.ui.DrawerDisplayMode;

public class NavigationDrawerApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		EmbeddedSocial.init(this, R.raw.embedded_social_config);
		EmbeddedSocial.initDrawer(this, new DrawerFactory(), DrawerDisplayMode.TABS, "STUDIO");
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}
}
