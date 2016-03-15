/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.event.click;

import com.microsoft.socialplus.base.event.AbstractEvent;
import com.microsoft.socialplus.base.event.HandlingThread;
import com.microsoft.socialplus.base.event.ThreadType;
import com.microsoft.socialplus.server.model.view.TopicView;

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
