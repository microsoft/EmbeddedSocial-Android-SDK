/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.data.storage.request.wrapper.relationship;

import com.microsoft.socialplus.data.storage.UserCache;
import com.microsoft.socialplus.data.storage.request.wrapper.AbstractBatchNetworkMethodWrapper;
import com.microsoft.socialplus.server.model.FeedUserRequest;
import com.microsoft.socialplus.server.model.UsersListResponse;

import java.sql.SQLException;

public class UserFeedRequestWrapper
	extends AbstractBatchNetworkMethodWrapper<FeedUserRequest, UsersListResponse> {

	protected final UserCache userCache;
	protected final UserCache.UserFeedType feedType;

	public UserFeedRequestWrapper(
		INetworkMethod<FeedUserRequest, UsersListResponse> networkMethod,
		UserCache userCache,
		UserCache.UserFeedType feedType) {

		super(networkMethod);
		this.userCache = userCache;
		this.feedType = feedType;
	}

	@Override
	protected void storeResponse(FeedUserRequest batchUserRequest,
	                             UsersListResponse usersListResponse) throws SQLException {

		userCache.storeUserFeed(batchUserRequest, feedType, usersListResponse);
	}

	@Override
	protected UsersListResponse getCachedResponse(FeedUserRequest batchUserRequest)
		throws SQLException {

		return userCache.getResponse(feedType);
	}
}
