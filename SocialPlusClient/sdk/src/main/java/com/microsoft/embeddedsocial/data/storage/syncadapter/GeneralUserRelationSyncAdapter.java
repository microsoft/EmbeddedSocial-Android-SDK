/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage.syncadapter;

import com.microsoft.embeddedsocial.server.model.relationship.AcceptFollowRequest;
import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.data.storage.UserCache;
import com.microsoft.embeddedsocial.data.storage.model.UserRelationOperation;
import com.microsoft.embeddedsocial.server.IRelationshipService;
import com.microsoft.embeddedsocial.server.EmbeddedSocialServiceProvider;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.relationship.BlockUserRequest;
import com.microsoft.embeddedsocial.server.model.relationship.RejectFollowRequest;
import com.microsoft.embeddedsocial.server.model.relationship.UnblockUserRequest;
import com.microsoft.embeddedsocial.server.model.relationship.UnfollowUserRequest;
import com.microsoft.embeddedsocial.server.model.relationship.UserRelationshipRequest;

import retrofit2.Response;

/**
 * User relationship operation sync adapter for operations that don't require
 * DB consistency rules execution.
 */
public class GeneralUserRelationSyncAdapter extends AbstractUserRelationSyncAdapter<Response> {

	private static final IRelationshipService SERVICE = GlobalObjectRegistry
			.getObject(EmbeddedSocialServiceProvider.class)
			.getRelationshipService();

	public GeneralUserRelationSyncAdapter(UserRelationOperation operation,
	                                      UserCache userCache) {
		super(operation, userCache);
	}

	@Override
	protected Response performNetworkRequest(UserRelationshipRequest request)
		throws NetworkRequestException {

		UserCache.UserRelationAction action = operation.getAction();
		String handle = request.getRelationshipUserHandle();
		Response response;
		switch (action) {
			case UNFOLLOW:
				response = SERVICE.unfollowUser(new UnfollowUserRequest(handle));
				break;
			case BLOCK:
				response = SERVICE.blockUser(new BlockUserRequest(handle));
				break;
			case UNBLOCK:
				response = SERVICE.unblockUser(new UnblockUserRequest(handle));
				break;
			case ACCEPT:
				response = SERVICE.acceptUser(new AcceptFollowRequest(handle));
				break;
			case REJECT:
				response = SERVICE.rejectUser(new RejectFollowRequest(handle));
				break;
			default:
				throw new NetworkRequestException("unknown operation: " + action.name());
		}
		return response;
	}

	@Override
	protected void onSynchronizationCompleted(Response response) {
		userCache.deleteOperation(operation);
	}
}
