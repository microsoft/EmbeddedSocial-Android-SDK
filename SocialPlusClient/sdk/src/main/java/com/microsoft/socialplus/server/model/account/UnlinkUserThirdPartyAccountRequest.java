/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.account;

import com.microsoft.autorest.models.IdentityProvider;
import com.microsoft.socialplus.server.model.UserRequest;

public class UnlinkUserThirdPartyAccountRequest extends UserRequest {

	private IdentityProvider identityProvider;

	public UnlinkUserThirdPartyAccountRequest(IdentityProvider identityProvider) {
		this.identityProvider = identityProvider;
	}
}
