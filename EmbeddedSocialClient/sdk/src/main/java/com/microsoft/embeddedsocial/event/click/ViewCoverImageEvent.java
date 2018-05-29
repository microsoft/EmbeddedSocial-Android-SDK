/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.event.click;

import com.microsoft.embeddedsocial.base.event.HandlingThread;
import com.microsoft.embeddedsocial.base.event.ThreadType;
import com.microsoft.embeddedsocial.event.BaseCommonBehaviorEvent;
import com.microsoft.embeddedsocial.server.model.view.TopicView;

import android.support.v4.app.Fragment;

/**
 * View selected cover image.
 */
@HandlingThread(ThreadType.MAIN)
public class ViewCoverImageEvent extends BaseCommonBehaviorEvent {
    private final TopicView topic;

    public ViewCoverImageEvent(Fragment source, TopicView topic) {
        super(source);
        this.topic = topic;
    }

    public TopicView getTopic() {
        return topic;
    }
}
