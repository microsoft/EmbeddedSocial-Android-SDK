/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage.request.wrapper.relationship;

import com.microsoft.embeddedsocial.server.model.UsersListResponse;
import com.microsoft.embeddedsocial.data.storage.UserCache;
import com.microsoft.embeddedsocial.data.storage.request.wrapper.AbstractBatchNetworkMethodWrapper;
import com.microsoft.embeddedsocial.server.model.relationship.GetPendingUsersRequest;

import java.sql.SQLException;

public class PendingUserFeedRequestWrapper
	extends AbstractBatchNetworkMethodWrapper<GetPendingUsersRequest, UsersListResponse> {

	protected final UserCache userCache;
	protected final UserCache.UserFeedType feedType;

	public PendingUserFeedRequestWrapper(
		INetworkMethod<GetPendingUsersRequest, UsersListResponse> networkMethod,
		UserCache userCache,
		UserCache.UserFeedType feedType) {

		super(networkMethod);
		this.userCache = userCache;
		this.feedType = feedType;
	}

	@Override
	protected void storeResponse(GetPendingUsersRequest batchUserRequest,
	                             UsersListResponse usersListResponse) throws SQLException {

		userCache.storeUserFeed(batchUserRequest, feedType, usersListResponse);
	}

	@Override
	protected UsersListResponse getCachedResponse(GetPendingUsersRequest batchUserRequest)
		throws SQLException {

		return userCache.getResponse(feedType);
	}
}
