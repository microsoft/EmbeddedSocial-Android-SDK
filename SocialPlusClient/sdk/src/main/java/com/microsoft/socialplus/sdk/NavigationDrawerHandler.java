/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.sdk;

import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;

public interface NavigationDrawerHandler {
    /**
     * Fragment to be used in the navigation drawer
     */
    public Fragment getFragment();

    /**
     * Do any final set up before the fragment is displayed
     * @param activity hosting the fragment
     * @param fragmentId of fragment
     * @param drawerLayout containing fragment
     */
    public void setUp(FragmentActivity activity, int fragmentId, DrawerLayout drawerLayout);

    /**
     * Returns the resource ID defining the navigation drawer width
     */
    public @DimenRes int getWidth();

    /**
     * Returns the resource ID defining the navigation drawer background color
     */
    public @ColorRes int getBackgroundColor();
}
