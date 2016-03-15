/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.data.storage.syncadapter;

import com.microsoft.socialplus.base.GlobalObjectRegistry;
import com.microsoft.socialplus.data.storage.UserCache;
import com.microsoft.socialplus.data.storage.model.UserRelationOperation;
import com.microsoft.socialplus.server.SocialPlusServiceProvider;
import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.microsoft.socialplus.server.model.relationship.FollowUserRequest;
import com.microsoft.socialplus.server.model.relationship.FollowUserResponse;
import com.microsoft.socialplus.server.model.relationship.UserRelationshipRequest;

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

		return GlobalObjectRegistry.getObject(SocialPlusServiceProvider.class)
			.getRelationshipService()
			.followUser(new FollowUserRequest(request.getRelationshipUserHandle()));
	}

	@Override
	protected void onSynchronizationCompleted(FollowUserResponse followUserResponse) {
		userCache.deleteOperation(operation);
	}
}
