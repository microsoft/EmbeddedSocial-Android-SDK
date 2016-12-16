/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.event.dialog;

import com.microsoft.embeddedsocial.base.event.AbstractEvent;

/**
 * Base class for dialog button events.
 */
public class OnDialogButtonClickedEvent extends AbstractEvent {

	private final String dialogId;

	protected OnDialogButtonClickedEvent(String dialogId) {
		this.dialogId = dialogId;
	}

	public String getDialogId() {
		return dialogId;
	}
}
