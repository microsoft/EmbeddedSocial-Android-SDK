/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.sdk;

import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;

public interface INavigationDrawerHandler {
    /**
     * @return fragment to be used in the navigation drawer
     */
    public Fragment getFragment();

    /**
     * Do any final set up before the fragment is displayed
     * @param fragmentId of fragment
     * @param drawerLayout containing fragment
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout);

    /**
     * @return the resource ID defining the navigation drawer width
     */
    public @DimenRes int getWidth();

    /**
     * @return resource ID defining the navigation drawer background color
     */
    public @ColorRes int getBackgroundColor();

    /**
     * @return true if the toolbar should be visible when the navigation drawer is open
     */
    public boolean displayToolbar();
}
