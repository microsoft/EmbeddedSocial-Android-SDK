/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.event.click;

import com.microsoft.socialplus.base.event.AbstractEvent;
import com.microsoft.socialplus.base.event.HandlingThread;
import com.microsoft.socialplus.base.event.ThreadType;
import com.microsoft.socialplus.server.model.view.TopicView;

/**
 * Open selected topic.
 */
@HandlingThread(ThreadType.MAIN)
public class OpenTopicEvent extends AbstractEvent {

	private final TopicView topic;
	private final boolean jumpToEdit;

	public OpenTopicEvent(TopicView topic) {
		this(topic, false);
	}

	public OpenTopicEvent(TopicView topic, boolean jumpToEdit) {
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
