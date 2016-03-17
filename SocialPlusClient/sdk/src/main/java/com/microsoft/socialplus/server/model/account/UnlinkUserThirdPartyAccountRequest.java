/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.account;

import com.microsoft.autorest.models.IdentityProvider;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.socialplus.server.model.UserRequest;

import java.io.IOException;

import retrofit2.Response;

public class UnlinkUserThirdPartyAccountRequest extends UserRequest {

	private IdentityProvider identityProvider;

	public UnlinkUserThirdPartyAccountRequest(IdentityProvider identityProvider) {
		this.identityProvider = identityProvider;
	}

	@Override
	public Response send() throws ServiceException, IOException {
		ServiceResponse<Object> serviceResponse =
				LINKED_ACCOUNTS.deleteLinkedAccount(identityProvider.toValue(), bearerToken);
		return serviceResponse.getResponse();
	}
}
