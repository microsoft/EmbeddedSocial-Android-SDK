/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.activity;

import com.microsoft.embeddedsocial.ui.activity.base.BaseActivity;
import com.microsoft.embeddedsocial.ui.fragment.CreateProfileFragment;

import android.os.Bundle;

/**
 * Activity for creating a user's profile.
 */
public class CreateProfileActivity extends BaseActivity {

    @Override
    protected void setupFragments() {
        CreateProfileFragment createProfileFragment = new CreateProfileFragment();
        setActivityContent(createProfileFragment);
        createProfileFragment.setArguments(getIntent().getExtras());
    }
}
