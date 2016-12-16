/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.event.content;

import com.microsoft.embeddedsocial.base.event.HandlingThread;
import com.microsoft.embeddedsocial.data.model.RemoveContentData;
import com.microsoft.embeddedsocial.base.event.ThreadType;

/**
 * Result of remove comment.
 */
@HandlingThread(ThreadType.MAIN)
public class TopicRemovedEvent extends ContentRemovedEvent {

	public TopicRemovedEvent(RemoveContentData data, boolean result) {
		super(data, result);
	}
}
