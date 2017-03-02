/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.fetcher;

import com.microsoft.embeddedsocial.fetcher.base.RequestType;
import com.microsoft.embeddedsocial.server.model.view.UserCompactView;
import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.data.model.FollowRequest;
import com.microsoft.embeddedsocial.fetcher.base.DataState;
import com.microsoft.embeddedsocial.fetcher.base.Fetcher;
import com.microsoft.embeddedsocial.server.IRelationshipService;
import com.microsoft.embeddedsocial.server.EmbeddedSocialServiceProvider;
import com.microsoft.embeddedsocial.server.model.relationship.GetPendingUsersRequest;

import java.util.List;

/**
 * Fetches new follow requests.
 */
class FollowRequestsFetcher extends Fetcher<FollowRequest> {

	private final BatchDataRequestExecutor<UserCompactView, GetPendingUsersRequest> requestExecutor;

	public FollowRequestsFetcher() {
		IRelationshipService server = GlobalObjectRegistry.getObject(EmbeddedSocialServiceProvider.class).getRelationshipService();
		requestExecutor = new BatchDataRequestExecutor<>(server::getUserPendingFeed, GetPendingUsersRequest::new);
	}

	@Override
	protected List<FollowRequest> fetchDataPage(DataState dataState, RequestType requestType, int pageSize) throws Exception {
		// just wrap the response in a list of our models
		List<UserCompactView> users = requestExecutor.fetchData(dataState, requestType, pageSize);
		return FollowRequest.wrap(users);
	}
}
