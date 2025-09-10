/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.navigationdrawerstudio;

import com.microsoft.embeddedsocial.sdk.EmbeddedSocial;
import com.microsoft.embeddedsocial.sdk.ui.DrawerDisplayMode;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

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
