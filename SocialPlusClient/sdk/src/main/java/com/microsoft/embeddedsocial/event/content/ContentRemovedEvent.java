/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.event.content;

import com.microsoft.embeddedsocial.base.event.HandlingThread;
import com.microsoft.embeddedsocial.data.model.RemoveContentData;
import com.microsoft.embeddedsocial.base.event.AbstractEvent;
import com.microsoft.embeddedsocial.base.event.ThreadType;

/**
 * Base class to remove any kind of content.
 */
@HandlingThread(ThreadType.MAIN)
abstract class ContentRemovedEvent extends AbstractEvent {

	private final RemoveContentData data;
	private final boolean success;

	public ContentRemovedEvent(RemoveContentData data, boolean success) {
		this.data = data;
		this.success = success;
	}

	public RemoveContentData getData() {
		return data;
	}

	public boolean isSuccessful() {
		return success;
	}
}
