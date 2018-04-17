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
 * Open selected topic.
 */
@HandlingThread(ThreadType.MAIN)
public class OpenTopicEvent extends BaseCommonBehaviorEvent {

    private final TopicView topic;
    private final boolean jumpToEdit;

    public OpenTopicEvent(Fragment source, TopicView topic) {
        this(source, topic, false);
    }

    public OpenTopicEvent(Fragment source, TopicView topic, boolean jumpToEdit) {
        super(source);
        this.topic = topic;
        this.jumpToEdit = jumpToEdit;
    }

    public TopicView getTopic() {
        return topic;
    }

    public boolean jumpToEdit() {
        return jumpToEdit;
    }
}
