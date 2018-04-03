/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.event.click;

import com.microsoft.embeddedsocial.base.event.HandlingThread;
import com.microsoft.embeddedsocial.base.event.ThreadType;
import com.microsoft.embeddedsocial.event.BaseCommonBehaviorEvent;
import com.microsoft.embeddedsocial.server.model.view.UserCompactView;

import android.support.v4.app.Fragment;

/**
 * Open selected user profile.
 */
@HandlingThread(ThreadType.CALLING_MAIN)
public class OpenUserProfileEvent extends BaseCommonBehaviorEvent {

	private final UserCompactView user;

	public OpenUserProfileEvent(Fragment source, UserCompactView user) {
		super(source);
		this.user = user;
	}

	public UserCompactView getUser() {
		return user;
	}

}
