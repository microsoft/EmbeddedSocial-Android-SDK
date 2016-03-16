/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.relationship;

import com.microsoft.autorest.models.FeedResponseUserCompactView;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.socialplus.server.model.UsersListResponse;

import java.io.IOException;

public final class GetFollowerFeedRequest extends GetFollowFeedRequest {

	public GetFollowerFeedRequest(String queryUserHandle) {
		super(queryUserHandle);
	}

	@Override
	public UsersListResponse send() throws ServiceException, IOException {
		ServiceResponse<FeedResponseUserCompactView> serviceResponse =
				FOLLOWERS.getFollowers(bearerToken, getCursor(), getBatchSize());
		return new UsersListResponse(serviceResponse.getBody());
	}
}
