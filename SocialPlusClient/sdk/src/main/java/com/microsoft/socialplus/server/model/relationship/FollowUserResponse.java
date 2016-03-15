/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.relationship;

import com.microsoft.autorest.models.FollowingStatus;
import com.microsoft.socialplus.base.utils.EnumUtils;

public class FollowUserResponse {

	private FollowingStatus followingStatus;

	public FollowUserResponse(FollowingStatus followingStatus) {
		this.followingStatus = followingStatus;
	}
	public FollowingStatus getFollowingStatus() {
		return followingStatus;
	}
}
