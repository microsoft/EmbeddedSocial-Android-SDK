/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.event;

import com.microsoft.socialplus.base.event.AbstractEvent;
import com.microsoft.socialplus.base.utils.ObjectUtils;

/**
 * Base class for events related to a user.
 */
public abstract class BaseUserEvent extends AbstractEvent {
	final String userHandle;

	public BaseUserEvent(String userHandle) {
		this.userHandle = userHandle;
	}

	public String getUserHandle() {
		return userHandle;
	}

	public boolean isForUser(String anotherUserHandle) {
		return ObjectUtils.equal(userHandle, anotherUserHandle);
	}
}
