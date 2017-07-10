/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.event;

import com.microsoft.embeddedsocial.base.event.HandlingThread;
import com.microsoft.embeddedsocial.base.event.AbstractEvent;
import com.microsoft.embeddedsocial.base.event.ThreadType;

/**
 * Scroll selected position out from screen.
 */
@HandlingThread(ThreadType.CALLING_MAIN)
public class ScrollPositionEvent extends AbstractEvent {
	public static final int EDIT_POSITION = -1;
	final int position;

	public ScrollPositionEvent(int position) {
		this.position = position;
	}

	public int getPosition() {
		return position;
	}
}
