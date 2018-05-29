/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.navigationdrawerstudio;

import com.microsoft.embeddedsocial.sdk.INavigationDrawerFactory;

import android.support.v4.app.Fragment;

/**
 * Create navigation menu to inflate to the Social+ SDK.
 */
public class DrawerFactory implements INavigationDrawerFactory {

    @Override
    public Fragment createMenuFragment() {
        NavigationDrawerFragment navigationDrawerFragment = new NavigationDrawerFragment();
        return navigationDrawerFragment;
    }
}
