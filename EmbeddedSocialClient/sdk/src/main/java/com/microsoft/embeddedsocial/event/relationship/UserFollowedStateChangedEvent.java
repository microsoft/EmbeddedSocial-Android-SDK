/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.event.relationship;

import com.microsoft.embeddedsocial.base.event.HandlingThread;
import com.microsoft.embeddedsocial.autorest.models.FollowerStatus;
import com.microsoft.embeddedsocial.base.event.ThreadType;
import com.microsoft.embeddedsocial.event.BaseUserEvent;

/**
 * User followed status has been changed event.
 */
@HandlingThread(ThreadType.CALLING_MAIN)
public class UserFollowedStateChangedEvent extends BaseUserEvent {

	private final FollowerStatus followedState;

	public UserFollowedStateChangedEvent(String userHandle, FollowerStatus followedState) {
		super(userHandle);
		this.followedState = followedState;
	}

	public FollowerStatus getFollowedStatus() {
		return followedState;
	}
}
