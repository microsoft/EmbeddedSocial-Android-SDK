/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.event.click;

import com.microsoft.socialplus.base.event.AbstractEvent;
import com.microsoft.socialplus.base.event.HandlingThread;
import com.microsoft.socialplus.base.event.ThreadType;
import com.microsoft.socialplus.server.model.view.UserCompactView;

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
