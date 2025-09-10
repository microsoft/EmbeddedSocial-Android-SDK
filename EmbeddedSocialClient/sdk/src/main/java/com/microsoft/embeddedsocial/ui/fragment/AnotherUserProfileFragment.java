/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.fragment;

import com.microsoft.embeddedsocial.service.IntentExtras;
import com.microsoft.embeddedsocial.ui.fragment.base.BaseProfileFragment;

import android.app.Activity;
import android.content.Intent;

/**
 * Fragment displaying another user's profile
 */
public class AnotherUserProfileFragment extends BaseProfileFragment {
    @Override
    protected void initExtraVariables() {
        Activity activity = getActivity();
        Intent intent = activity.getIntent();
        setUserHandle(intent.getStringExtra(IntentExtras.USER_HANDLE));
        setUserName(intent.getStringExtra(IntentExtras.NAME));
        setIsCurrentUser(false);
        setFeedIsReadable(!intent.getBooleanExtra(IntentExtras.FEED_IS_NOT_READABLE, false));
    }
}
