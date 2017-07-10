/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.event.content;

import com.microsoft.embeddedsocial.base.event.HandlingThread;
import com.microsoft.embeddedsocial.base.event.ThreadType;
import com.microsoft.embeddedsocial.data.model.PinData;

/**
 * Result of add pin.
 */
@HandlingThread(ThreadType.MAIN)
public class PinAddedEvent extends PinEvent {

	public PinAddedEvent(PinData data, boolean result) {
		super(data, result);
	}
}
