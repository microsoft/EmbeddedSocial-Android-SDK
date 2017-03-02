/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage.syncadapter;

import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.data.storage.model.UserRelationOperation;
import com.microsoft.embeddedsocial.server.EmbeddedSocialServiceProvider;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.relationship.FollowUserResponse;
import com.microsoft.embeddedsocial.server.model.relationship.UserRelationshipRequest;
import com.microsoft.embeddedsocial.data.storage.UserCache;
import com.microsoft.embeddedsocial.server.model.relationship.FollowUserRequest;

/**
 * Sync adapter for 'follow user' relationship operation.
 */
public class FollowUserSyncAdapter extends AbstractUserRelationSyncAdapter<FollowUserResponse> {

	public FollowUserSyncAdapter(UserRelationOperation operation, UserCache userCache) {
		super(operation, userCache);
	}

	@Override
	protected FollowUserResponse performNetworkRequest(UserRelationshipRequest request)
		throws NetworkRequestException {

		return GlobalObjectRegistry.getObject(EmbeddedSocialServiceProvider.class)
			.getRelationshipService()
			.followUser(new FollowUserRequest(request.getRelationshipUserHandle()));
	}

	@Override
	protected void onSynchronizationCompleted(FollowUserResponse followUserResponse) {
		userCache.deleteOperation(operation);
	}
}
