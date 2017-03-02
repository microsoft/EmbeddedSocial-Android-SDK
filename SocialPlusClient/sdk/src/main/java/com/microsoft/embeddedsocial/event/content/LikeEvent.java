/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.event.content;

import com.microsoft.embeddedsocial.base.event.AbstractEvent;
import com.microsoft.embeddedsocial.base.event.HandlingThread;
import com.microsoft.embeddedsocial.base.event.ThreadType;
import com.microsoft.embeddedsocial.data.model.LikeContentData;

/**
 * Base class for like operations.
 */
@HandlingThread(ThreadType.MAIN)
abstract class LikeEvent extends AbstractEvent {
	private final LikeContentData data;
	private final boolean result;

	public LikeEvent(LikeContentData data, boolean result) {
		this.data = data;
		this.result = result;
	}

	public LikeContentData getData() {
		return data;
	}

	public boolean isResult() {
		return result;
	}
}
