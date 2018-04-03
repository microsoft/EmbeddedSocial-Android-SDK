/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.fragment;

import com.microsoft.embeddedsocial.account.UserAccount;
import com.microsoft.embeddedsocial.ui.fragment.base.BaseProfileFragment;

/**
 * Fragment displaying the current user's profile
 */
public class MyProfileFragment extends BaseProfileFragment {
    public static final String TAG = "MyProfileFragment";

    @Override
    protected void initExtraVariables() {
        UserAccount currentUser = UserAccount.getInstance();
        setUserHandle(currentUser.getUserHandle());
        setUserName(currentUser.getAccountDetails().getFullName());
        setIsCurrentUser(true);
        setFeedIsReadable(true);
    }
}
