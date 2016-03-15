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
public class GetUserRequest extends UserRequest {

	private String queryUserHandle;

	public GetUserRequest(String queryUserHandle) {
		this.queryUserHandle = queryUserHandle;
	}
}
