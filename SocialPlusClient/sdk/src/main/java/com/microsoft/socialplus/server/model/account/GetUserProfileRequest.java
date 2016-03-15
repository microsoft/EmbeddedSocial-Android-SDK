/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.account;

import com.microsoft.socialplus.server.model.UserRequest;

/**
 *
 */
public class GetUserProfileRequest extends UserRequest {

	private final String queryUserHandle;
	private final String queryUsername;

	public GetUserProfileRequest(String queryUserHandle) {
		this(queryUserHandle, null);
	}

	public GetUserProfileRequest(String queryUserHandle, String queryUsername) {
		this.queryUserHandle = queryUserHandle;
		this.queryUsername = queryUsername;
	}

	public String getQueryUserHandle() {
		return queryUserHandle;
	}
}
