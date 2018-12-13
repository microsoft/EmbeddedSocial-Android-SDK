/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.event;

import com.microsoft.embeddedsocial.base.event.AbstractEvent;

import androidx.fragment.app.Fragment;

/**
 * Base class for events posted by a variety of fragments.
 * Stores a reference to the fragment which posted the event so that events can be filtered by sender.
 */
public class BaseCommonBehaviorEvent extends AbstractEvent {
    private Fragment source;

    public BaseCommonBehaviorEvent(Fragment source) {
        this.source = source;
    }

    public Fragment getSource() {
        return source;
    }
}
