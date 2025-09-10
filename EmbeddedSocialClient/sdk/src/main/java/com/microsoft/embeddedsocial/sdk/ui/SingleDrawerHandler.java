/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.sdk.ui;

import com.microsoft.embeddedsocial.ui.fragment.NavigationFragment;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class SingleDrawerHandler extends DrawerHandler {
    public SingleDrawerHandler(@NonNull AppCompatActivity activity) {
        super(activity, null, null);
    }

    @Override
    public void inflate(ViewGroup drawerContainer, int embeddedSocialMenuActiveItemId) {
        super.inflate(drawerContainer, embeddedSocialMenuActiveItemId);

        embeddedSocialMenuFragment = NavigationFragment.create(embeddedSocialMenuActiveItemId);

        FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(drawerContainer.getId(), embeddedSocialMenuFragment, SOCIAL_TAG);
        fragmentTransaction.commit();
    }
}
