/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.event.dialog;

import com.microsoft.embeddedsocial.base.event.HandlingThread;
import com.microsoft.embeddedsocial.base.event.ThreadType;

/**
 * Dialog item was selected event.
 */
@HandlingThread(ThreadType.CALLING_MAIN)
public class OnDialogItemSelectedEvent extends OnDialogButtonClickedEvent {

	private final int which;

	private final int textId;

	public OnDialogItemSelectedEvent(String dialogId, int which, int textId) {
		super(dialogId);
		this.which = which;
		this.textId = textId;
	}

	public int getTextId() {
		return textId;
	}

	public int getWhich() {
		return which;
	}
}
