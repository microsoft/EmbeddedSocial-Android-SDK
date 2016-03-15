/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.data.storage.request.wrapper.content;

import com.microsoft.socialplus.data.storage.UserCache;
import com.microsoft.socialplus.data.storage.request.wrapper.AbstractNetworkMethodWrapper;
import com.microsoft.socialplus.server.model.UsersListResponse;
import com.microsoft.socialplus.server.model.like.GetLikeFeedRequest;

import java.sql.SQLException;

public class LikeFeedRequestWrapper extends AbstractNetworkMethodWrapper<GetLikeFeedRequest, UsersListResponse> {

	private final UserCache userCache;

	public LikeFeedRequestWrapper(INetworkMethod<GetLikeFeedRequest, UsersListResponse> networkMethod,
	                              UserCache userCache) {

		super(networkMethod);
		this.userCache = userCache;
	}

	@Override
	protected void storeResponse(GetLikeFeedRequest request, UsersListResponse response)
		throws SQLException {

		userCache.storeLikeFeed(request, response);
	}

	@Override
	protected UsersListResponse getCachedResponse(GetLikeFeedRequest request)
		throws SQLException {

		return userCache.getResponse(UserCache.UserFeedType.LIKED, request.getContentHandle());
	}
}
