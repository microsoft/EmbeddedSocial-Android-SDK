/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.event.click;

import com.microsoft.embeddedsocial.base.event.AbstractEvent;
import com.microsoft.embeddedsocial.base.event.HandlingThread;
import com.microsoft.embeddedsocial.server.model.view.UserCompactView;
import com.microsoft.embeddedsocial.base.event.ThreadType;

/**
 * Open selected user profile.
 */
@HandlingThread(ThreadType.CALLING_MAIN)
public class OpenUserProfileEvent extends AbstractEvent {

	private final UserCompactView user;

	public OpenUserProfileEvent(UserCompactView user) {
		this.user = user;
	}

	public UserCompactView getUser() {
		return user;
	}

}
