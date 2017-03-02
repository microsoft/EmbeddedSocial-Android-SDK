/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.event.content;

import com.microsoft.embeddedsocial.base.event.AbstractEvent;
import com.microsoft.embeddedsocial.base.event.HandlingThread;
import com.microsoft.embeddedsocial.base.event.ThreadType;
import com.microsoft.embeddedsocial.data.model.PinData;

/**
 * Base class for pin operations.
 */
@HandlingThread(ThreadType.MAIN)
abstract class PinEvent extends AbstractEvent {
	private final PinData data;
	private final boolean result;

	public PinEvent(PinData data, boolean result) {
		this.data = data;
		this.result = result;
	}

	public PinData getData() {
		return data;
	}

	public boolean isResult() {
		return result;
	}
}
