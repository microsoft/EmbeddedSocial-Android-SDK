/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.event.relationship;

import com.microsoft.socialplus.autorest.models.FollowerStatus;
import com.microsoft.socialplus.base.event.HandlingThread;
import com.microsoft.socialplus.base.event.ThreadType;
import com.microsoft.socialplus.event.BaseUserEvent;

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
