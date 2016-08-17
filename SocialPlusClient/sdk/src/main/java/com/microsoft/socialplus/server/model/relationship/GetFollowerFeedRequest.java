/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.relationship;

import com.microsoft.socialplus.autorest.UserFollowersOperations;
import com.microsoft.socialplus.autorest.UserFollowersOperationsImpl;
import com.microsoft.socialplus.autorest.models.FeedResponseUserCompactView;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.microsoft.socialplus.server.model.UsersListResponse;

import java.io.IOException;

public final class GetFollowerFeedRequest extends GetFollowFeedRequest {
	private static final UserFollowersOperations USER_FOLLOWERS =
			new UserFollowersOperationsImpl(RETROFIT, CLIENT);

	public GetFollowerFeedRequest(String queryUserHandle) {
		super(queryUserHandle);
	}

	@Override
	public UsersListResponse send() throws NetworkRequestException {
		ServiceResponse<FeedResponseUserCompactView> serviceResponse;
		try {
			serviceResponse = USER_FOLLOWERS.getFollowers(getQueryUserHandle(), authorization,
					getCursor(), getBatchSize());
		} catch (ServiceException|IOException e) {
			throw new NetworkRequestException(e.getMessage());
		}
		checkResponseCode(serviceResponse);

		return new UsersListResponse(serviceResponse.getBody());
	}
}
