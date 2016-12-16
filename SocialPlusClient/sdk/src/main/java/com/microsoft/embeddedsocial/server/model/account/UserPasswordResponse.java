/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model.account;

public class UserPasswordResponse {

	private String userHandle;
	private String accessToken;

	public String getUserHandle() {
		return userHandle;
	}

	public String getAccessToken() {
		return accessToken;
	}
}
