/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.sdk.ui;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.microsoft.socialplus.ui.fragment.NavigationFragment;

public class SingleDrawerHandler extends DrawerHandler {
	public SingleDrawerHandler(@NonNull AppCompatActivity activity) {
		super(activity, null, null);
	}

	@Override
	public void inflate(ViewGroup drawerContainer, int socialPlusMenuActiveItemId) {
		super.inflate(drawerContainer, socialPlusMenuActiveItemId);

		socialPlusMenuFragment = NavigationFragment.create(socialPlusMenuActiveItemId);

		FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
		fragmentTransaction.replace(drawerContainer.getId(), socialPlusMenuFragment, SOCIAL_TAG);
		fragmentTransaction.commit();
	}
}
