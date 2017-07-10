/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.event.action;


import com.microsoft.embeddedsocial.base.event.HandlingThread;
import com.microsoft.embeddedsocial.actions.Action;
import com.microsoft.embeddedsocial.base.event.ThreadType;

/**
 * Action started event.
 */
@HandlingThread(ThreadType.MAIN)
public class ActionStartedEvent extends ActionEvent {

	public ActionStartedEvent(Action action) {
		super(action);
	}

}
