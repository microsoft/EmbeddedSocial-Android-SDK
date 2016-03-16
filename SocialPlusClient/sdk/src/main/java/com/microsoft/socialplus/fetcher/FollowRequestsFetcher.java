/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.fetcher;

import com.microsoft.socialplus.base.GlobalObjectRegistry;
import com.microsoft.socialplus.data.model.FollowRequest;
import com.microsoft.socialplus.fetcher.base.DataState;
import com.microsoft.socialplus.fetcher.base.Fetcher;
import com.microsoft.socialplus.fetcher.base.RequestType;
import com.microsoft.socialplus.server.IRelationshipService;
import com.microsoft.socialplus.server.SocialPlusServiceProvider;
import com.microsoft.socialplus.server.model.FeedUserRequest;
import com.microsoft.socialplus.server.model.relationship.GetPendingUsersRequest;
import com.microsoft.socialplus.server.model.view.UserCompactView;

import java.util.List;

/**
 * Fetches new follow requests.
 */
class FollowRequestsFetcher extends Fetcher<FollowRequest> {

	private final BatchDataRequestExecutor<UserCompactView, GetPendingUsersRequest> requestExecutor;

	public FollowRequestsFetcher() {
		IRelationshipService server = GlobalObjectRegistry.getObject(SocialPlusServiceProvider.class).getRelationshipService();
		requestExecutor = new BatchDataRequestExecutor<>(server::getUserPendingFeed, GetPendingUsersRequest::new);
	}

	@Override
	protected List<FollowRequest> fetchDataPage(DataState dataState, RequestType requestType, int pageSize) throws Exception {
		// just wrap the response in a list of our models
		List<UserCompactView> users = requestExecutor.fetchData(dataState, requestType, pageSize);
		return FollowRequest.wrap(users);
	}
}
