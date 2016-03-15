/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.event.content;

import com.microsoft.socialplus.base.event.HandlingThread;
import com.microsoft.socialplus.base.event.ThreadType;
import com.microsoft.socialplus.data.model.DiscussionItem;

/**
 * Result of add comment.
 */
@HandlingThread(ThreadType.MAIN)
public class ReplyAddedEvent extends NoteAddedEvent {

	public ReplyAddedEvent(DiscussionItem data, String handle, boolean result) {
		super(data, handle, result);
	}
}
