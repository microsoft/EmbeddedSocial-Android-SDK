/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.account;

import com.microsoft.autorest.models.LinkedAccountView;
import com.microsoft.autorest.models.UserProfileView;
import com.microsoft.socialplus.server.model.view.UserAccountView;

import java.util.List;

/**
 *
 */
public class GetUserAccountResponse {

	private UserAccountView user;

	public GetUserAccountResponse(UserAccountView user) {
		this.user = user;
	}

	public UserAccountView getUser() {
		return user;
	}
}
