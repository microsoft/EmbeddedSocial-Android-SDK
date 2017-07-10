/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage.request.wrapper.account;

import com.microsoft.embeddedsocial.server.model.account.GetUserRequest;
import com.microsoft.embeddedsocial.data.storage.UserCache;
import com.microsoft.embeddedsocial.data.storage.request.wrapper.AbstractNetworkMethodWrapper;
import com.microsoft.embeddedsocial.server.model.account.GetUserResponse;

import java.sql.SQLException;

public class GetUserWrapper extends AbstractNetworkMethodWrapper<GetUserRequest, GetUserResponse> {

	private final UserCache userCache;

	public GetUserWrapper(INetworkMethod<GetUserRequest, GetUserResponse> networkMethod,
	                      UserCache userCache) {

		super(networkMethod);
		this.userCache = userCache;
	}

	@Override
	protected void storeResponse(GetUserRequest request, GetUserResponse response) throws SQLException {
		userCache.storeUser(response.getUser());
	}

	@Override
	protected GetUserResponse getCachedResponse(GetUserRequest request) throws SQLException {
		return new GetUserResponse(userCache.getUserByHandle(request.getUserHandle()));
	}
}
