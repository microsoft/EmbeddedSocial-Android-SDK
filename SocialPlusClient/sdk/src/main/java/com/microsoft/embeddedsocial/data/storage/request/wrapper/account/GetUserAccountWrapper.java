/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage.request.wrapper.account;

import com.microsoft.embeddedsocial.data.storage.UserCache;
import com.microsoft.embeddedsocial.data.storage.request.wrapper.AbstractNetworkMethodWrapper;
import com.microsoft.embeddedsocial.server.model.account.GetUserAccountRequest;
import com.microsoft.embeddedsocial.server.model.account.GetUserAccountResponse;

import java.sql.SQLException;

public class GetUserAccountWrapper extends AbstractNetworkMethodWrapper<GetUserAccountRequest, GetUserAccountResponse> {

	private final UserCache userCache;

	public GetUserAccountWrapper(INetworkMethod<GetUserAccountRequest, GetUserAccountResponse> networkMethod,
	                             UserCache userCache) {

		super(networkMethod);
		this.userCache = userCache;
	}

	@Override
	protected void storeResponse(GetUserAccountRequest request, GetUserAccountResponse response)
		throws SQLException {

		userCache.storeUserAccount(response.getUser());
	}

	@Override
	protected GetUserAccountResponse getCachedResponse(GetUserAccountRequest request)
		throws SQLException {

		return new GetUserAccountResponse(userCache.getUserAccount(request.getUserHandle()));
	}
}
