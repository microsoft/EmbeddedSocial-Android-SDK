/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.event;

import com.microsoft.embeddedsocial.base.event.AbstractEvent;

import android.support.v4.app.Fragment;

public class BaseCommonBehaviorEvent extends AbstractEvent {
    private Fragment source;

    public BaseCommonBehaviorEvent(Fragment source) {
        this.source = source;
    }

    public Fragment getSource() {
        return source;
    }
}
