/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model.auth;

import com.microsoft.embeddedsocial.autorest.models.PostSessionResponse;
import com.microsoft.embeddedsocial.autorest.models.PostUserResponse;

public class AuthenticationResponse {

	private String userHandle;
	private String sessionToken;

	public AuthenticationResponse(PostUserResponse response) {
		this.userHandle = response.getUserHandle();
		this.sessionToken = response.getSessionToken();
	}

	public AuthenticationResponse(PostSessionResponse response) {
		this.userHandle = response.getUserHandle();
		this.sessionToken = response.getSessionToken();
	}

	public String getUserHandle() {
		return userHandle;
	}

	public String getSessionToken() {
		return sessionToken;
	}
}
