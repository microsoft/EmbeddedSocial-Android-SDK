/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.event.content;

import com.microsoft.embeddedsocial.base.event.HandlingThread;
import com.microsoft.embeddedsocial.data.model.RemoveContentData;
import com.microsoft.embeddedsocial.base.event.ThreadType;

/**
 * Result of add comment.
 */
@HandlingThread(ThreadType.MAIN)
public class ReplyRemovedEvent extends ContentRemovedEvent {

	public ReplyRemovedEvent(RemoveContentData data, boolean result) {
		super(data, result);
	}
}
