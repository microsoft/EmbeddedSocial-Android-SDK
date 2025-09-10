/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.activity;

import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.ui.activity.base.BaseActivity;
import com.microsoft.embeddedsocial.ui.fragment.ActivityFeedFragment;

/**
 * Shows the recent activity feed.
 */
public class ActivityFeedActivity extends BaseActivity {
    public static final String NAME = "ActivityFeed";

    public ActivityFeedActivity() {
        super(R.id.es_navigationActivity);
    }

    @Override
    protected void setupFragments() {
        setActivityContent(new ActivityFeedFragment());
        super.setupFragments();
    }

    @Override
    protected boolean isAuthorizationRequired() {
        return true;
    }

    @Override
    protected String getName() {
        return NAME;
    }
}
