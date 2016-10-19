/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.event.action;

import com.microsoft.socialplus.actions.Action;
import com.microsoft.socialplus.base.event.HandlingThread;
import com.microsoft.socialplus.base.event.ThreadType;

/**
 * Action was completed event.
 */
@HandlingThread(ThreadType.MAIN)
public class ActionCompletedEvent extends ActionEvent {

	public ActionCompletedEvent(Action action) {
		super(action);
	}

}
