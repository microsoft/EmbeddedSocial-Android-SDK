/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.activity.base;

import com.microsoft.embeddedsocial.base.event.EventBus;
import com.microsoft.embeddedsocial.event.data.ProfileDataUpdatedEvent;
import com.squareup.otto.Subscribe;

import androidx.appcompat.app.ActionBar;

/**
 * Base class for activities showing a profile.
 */
public abstract class BaseProfileActivity extends BaseActivity {
    protected String userHandle;

    protected BaseProfileActivity() {
    }

    protected BaseProfileActivity(int activeNavigationItemId) {
        super(activeNavigationItemId);
    }

    /**
     * Update the activity title in the action bar when the user name is updated
     */
    private final Object eventListener = new Object() {
        @Subscribe
        public void onProfileLoaded(ProfileDataUpdatedEvent event) {
            if (event.isForUser(userHandle)) {
                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setTitle(event.getAccountData().getFullName());
                }
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        EventBus.register(eventListener);
    }

    @Override
    public void onPause() {
        EventBus.unregister(eventListener);
        super.onPause();
    }
}
