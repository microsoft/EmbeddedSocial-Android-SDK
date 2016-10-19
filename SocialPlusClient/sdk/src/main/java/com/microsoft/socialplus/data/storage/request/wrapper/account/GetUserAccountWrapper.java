/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.data.storage.request.wrapper.account;

import com.microsoft.socialplus.data.storage.UserCache;
import com.microsoft.socialplus.data.storage.request.wrapper.AbstractNetworkMethodWrapper;
import com.microsoft.socialplus.server.model.UserRequest;
import com.microsoft.socialplus.server.model.account.GetUserAccountRequest;
import com.microsoft.socialplus.server.model.account.GetUserAccountResponse;

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
