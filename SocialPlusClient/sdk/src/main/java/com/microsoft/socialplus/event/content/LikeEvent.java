/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.event.content;

import com.microsoft.socialplus.base.event.AbstractEvent;
import com.microsoft.socialplus.base.event.HandlingThread;
import com.microsoft.socialplus.base.event.ThreadType;
import com.microsoft.socialplus.data.model.LikeContentData;

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
