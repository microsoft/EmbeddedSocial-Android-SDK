/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.event.click;

import com.microsoft.embeddedsocial.base.event.HandlingThread;
import com.microsoft.embeddedsocial.server.model.view.TopicView;
import com.microsoft.embeddedsocial.base.event.AbstractEvent;
import com.microsoft.embeddedsocial.base.event.ThreadType;

/**
 * View selected cover image.
 */
@HandlingThread(ThreadType.MAIN)
public class ViewCoverImageEvent extends AbstractEvent {
	private final TopicView topic;

	public ViewCoverImageEvent(TopicView topic) {
		this.topic = topic;
	}

	public TopicView getTopic() {
		return topic;
	}
}
