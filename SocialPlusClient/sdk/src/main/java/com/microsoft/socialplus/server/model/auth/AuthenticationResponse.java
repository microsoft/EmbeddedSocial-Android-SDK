/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.auth;

import com.microsoft.socialplus.autorest.models.PostSessionResponse;
import com.microsoft.socialplus.autorest.models.PostUserResponse;

public class AuthenticationResponse {

	private String userHandle;
	private String sessionTokenSignature;

	public AuthenticationResponse(PostUserResponse response) {
		this.userHandle = response.getUserHandle();
		this.sessionTokenSignature = response.getSessionToken();
	}

	public AuthenticationResponse(PostSessionResponse response) {
		this.userHandle = response.getUserHandle();
		this.sessionTokenSignature = response.getSessionToken();
	}

	public String getUserHandle() {
		return userHandle;
	}

	public String getSessionTokenSignature() {
		return sessionTokenSignature;
	}
}
