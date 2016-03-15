/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.account;

import com.microsoft.socialplus.server.model.view.UserAccountView;

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
