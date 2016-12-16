/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.event.dialog;

import com.microsoft.embeddedsocial.base.event.HandlingThread;
import com.microsoft.embeddedsocial.base.event.ThreadType;

/**
 * Dialog's positive button was clicked event.
 */
@HandlingThread(ThreadType.CALLING_MAIN)
public class OnPositiveButtonClickedEvent extends OnDialogButtonClickedEvent {

	public OnPositiveButtonClickedEvent(String dialogId) {
		super(dialogId);
	}

}
