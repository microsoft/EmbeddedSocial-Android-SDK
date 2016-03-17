/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.account;

import com.microsoft.socialplus.server.model.view.UserProfileView;

/**
 *
 */
public class GetUserProfileResponse {

	private UserProfileView user;

	public GetUserProfileResponse(UserProfileView user) {
		this.user = user;
	}

	public GetUserProfileResponse(com.microsoft.autorest.models.UserProfileView response) {
		user = new UserProfileView(response);
	}

	public UserProfileView getUser() {
		return user;
	}
}
