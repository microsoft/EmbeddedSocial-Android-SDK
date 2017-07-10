/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.event.relationship;

import com.microsoft.embeddedsocial.base.event.HandlingThread;
import com.microsoft.embeddedsocial.event.BaseUserEvent;
import com.microsoft.embeddedsocial.base.event.ThreadType;

/**
 * User has been unblocked event.
 */
@HandlingThread(ThreadType.CALLING_MAIN)
public class UserUnblockedEvent extends BaseUserEvent {
	public UserUnblockedEvent(String userHandle) {
		super(userHandle);
	}
}
