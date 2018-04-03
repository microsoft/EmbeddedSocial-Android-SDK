/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.event.content;

import com.microsoft.embeddedsocial.base.event.HandlingThread;
import com.microsoft.embeddedsocial.data.model.DiscussionItem;
import com.microsoft.embeddedsocial.base.event.ThreadType;

/**
 * Result of add comment.
 */
@HandlingThread(ThreadType.MAIN)
public class ReplyAddedEvent extends NoteAddedEvent {

	public ReplyAddedEvent(DiscussionItem data, String handle, boolean result) {
		super(data, handle, result);
	}
}
