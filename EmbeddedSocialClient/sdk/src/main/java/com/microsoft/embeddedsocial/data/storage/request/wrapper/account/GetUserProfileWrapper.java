/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage.request.wrapper.account;

import com.microsoft.embeddedsocial.server.model.account.GetUserProfileRequest;
import com.microsoft.embeddedsocial.data.storage.UserCache;
import com.microsoft.embeddedsocial.data.storage.request.wrapper.AbstractNetworkMethodWrapper;
import com.microsoft.embeddedsocial.server.model.account.GetUserProfileResponse;

import java.sql.SQLException;

public class GetUserProfileWrapper extends AbstractNetworkMethodWrapper<GetUserProfileRequest, GetUserProfileResponse> {

	private final UserCache userCache;

	public GetUserProfileWrapper(INetworkMethod<GetUserProfileRequest, GetUserProfileResponse> networkMethod,
	                             UserCache userCache) {

		super(networkMethod);
		this.userCache = userCache;
	}

	@Override
	protected void storeResponse(GetUserProfileRequest request, GetUserProfileResponse response)
		throws SQLException {

		userCache.storeUserProfile(response.getUser());
	}

	@Override
	protected GetUserProfileResponse getCachedResponse(GetUserProfileRequest request)
		throws SQLException {

		return new GetUserProfileResponse(userCache.getUserProfile(request.getQueryUserHandle()));
	}
}
