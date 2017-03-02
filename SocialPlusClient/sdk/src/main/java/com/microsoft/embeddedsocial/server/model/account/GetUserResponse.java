/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model.account;

import com.microsoft.embeddedsocial.server.model.view.UserCompactView;
import com.microsoft.embeddedsocial.autorest.models.UserProfileView;

/**
 *
 */
public class GetUserResponse {

	private UserCompactView user;

	public GetUserResponse(UserCompactView user) {
		this.user = user;
	}

	public GetUserResponse(UserProfileView view) {
		user = new UserCompactView(view);
	}

	public UserCompactView getUser() {
		return user;
	}
}
