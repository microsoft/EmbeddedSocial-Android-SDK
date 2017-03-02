/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.event.content;

import com.microsoft.embeddedsocial.base.event.HandlingThread;
import com.microsoft.embeddedsocial.data.model.PinData;
import com.microsoft.embeddedsocial.base.event.ThreadType;

/**
 * Result of add pin.
 */
@HandlingThread(ThreadType.MAIN)
public class PinRemovedEvent extends PinEvent {

	public PinRemovedEvent(PinData data, boolean result) {
		super(data, result);
	}
}
