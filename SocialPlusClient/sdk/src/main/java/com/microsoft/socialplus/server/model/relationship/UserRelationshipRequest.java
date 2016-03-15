/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.relationship;

import com.microsoft.autorest.MyBlockedUsersOperations;
import com.microsoft.autorest.MyBlockedUsersOperationsImpl;
import com.microsoft.autorest.MyFollowersOperations;
import com.microsoft.autorest.MyFollowersOperationsImpl;
import com.microsoft.autorest.MyFollowingOperations;
import com.microsoft.autorest.MyFollowingOperationsImpl;
import com.microsoft.autorest.MyPendingUsersOperations;
import com.microsoft.autorest.MyPendingUsersOperationsImpl;
import com.microsoft.socialplus.server.model.UserRequest;

public class UserRelationshipRequest extends UserRequest {

	protected static final MyFollowersOperations FOLLOWERS;
	protected static final MyFollowingOperations FOLLOWING;
	protected static final MyBlockedUsersOperations BLOCKED;
	protected static final MyPendingUsersOperations PENDING;

	static {
		FOLLOWERS = new MyFollowersOperationsImpl(RETROFIT, CLIENT);
		FOLLOWING = new MyFollowingOperationsImpl(RETROFIT, CLIENT);
		BLOCKED = new MyBlockedUsersOperationsImpl(RETROFIT, CLIENT);
		PENDING = new MyPendingUsersOperationsImpl(RETROFIT, CLIENT);
	}

	protected final String relationshipUserHandle;

	public UserRelationshipRequest(String relationshipUserHandle) {
		this.relationshipUserHandle = relationshipUserHandle;
	}

	public String getRelationshipUserHandle() {
		return relationshipUserHandle;
	}
}
