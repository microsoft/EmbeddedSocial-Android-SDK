/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model.discover;

import com.microsoft.embeddedsocial.autorest.models.IdentityProvider;

import com.microsoft.embeddedsocial.server.model.UserRequest;

import java.util.List;

public class FindUsersWithThirdPartyAccountsRequest extends UserRequest {

	private final IdentityProvider identityProvider;
	private final List<String> accountHandles;

	public FindUsersWithThirdPartyAccountsRequest(IdentityProvider identityProvider, List<String> accountHandles) {
		this.identityProvider = identityProvider;
		this.accountHandles = accountHandles;
	}
}
