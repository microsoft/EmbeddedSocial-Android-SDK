/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.data.storage;

import com.microsoft.socialplus.data.storage.request.wrapper.relationship.FollowFeedRequestWrapper;
import com.microsoft.socialplus.data.storage.request.wrapper.relationship.UserFeedRequestWrapper;
import com.microsoft.socialplus.server.IRelationshipService;
import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.microsoft.socialplus.server.model.FeedUserRequest;
import com.microsoft.socialplus.server.model.UsersListResponse;
import com.microsoft.socialplus.server.model.relationship.AcceptFollowRequest;
import com.microsoft.socialplus.server.model.relationship.BlockUserRequest;
import com.microsoft.socialplus.server.model.relationship.FollowUserRequest;
import com.microsoft.socialplus.server.model.relationship.FollowUserResponse;
import com.microsoft.socialplus.server.model.relationship.GetFollowFeedRequest;
import com.microsoft.socialplus.server.model.relationship.RejectFollowRequest;
import com.microsoft.socialplus.server.model.relationship.UnblockUserRequest;
import com.microsoft.socialplus.server.model.relationship.UnfollowUserRequest;

import retrofit2.Response;

/**
 * Provides transparent cache functionality on top of {@linkplain IRelationshipService}.
 */
public class RelationshipServiceCachingWrapper implements IRelationshipService {

	private final UserFeedRequestWrapper blockedUsersWrapper;
	private final UserFeedRequestWrapper pendingUsersWrapper;
	private final FollowFeedRequestWrapper userFollowerFeedWrapper;
	private final FollowFeedRequestWrapper userFollowingWrapper;

	private final IRelationshipService wrappedService;

	/**
	 * Creates an instance.
	 *
	 * @param wrappedService service to wrap
	 */
	public RelationshipServiceCachingWrapper(IRelationshipService wrappedService) {
		this.wrappedService = wrappedService;
		UserCache userCache = new UserCache();
		userFollowingWrapper = new FollowFeedRequestWrapper(
			wrappedService::getUserFollowingFeed,
			userCache,
			UserCache.UserFeedType.FOLLOWING
		);
		userFollowerFeedWrapper = new FollowFeedRequestWrapper(
			wrappedService::getUserFollowerFeed,
			userCache,
			UserCache.UserFeedType.FOLLOWER
		);
		blockedUsersWrapper = new UserFeedRequestWrapper(
			wrappedService::getUserBlockedFeed,
			userCache,
			UserCache.UserFeedType.BLOCKED
		);
		pendingUsersWrapper = new UserFeedRequestWrapper(
			wrappedService::getUserPendingFeed,
			userCache,
			UserCache.UserFeedType.PENDING
		);
	}

	@Override
	public Response acceptUser(AcceptFollowRequest request) throws NetworkRequestException {
		return wrappedService.acceptUser(request);
	}

	@Override
	public Response blockUser(BlockUserRequest request) throws NetworkRequestException {
		return wrappedService.blockUser(request);
	}

	@Override
	public FollowUserResponse followUser(FollowUserRequest request) throws NetworkRequestException {
		return wrappedService.followUser(request);
	}

	@Override
	public UsersListResponse getUserBlockedFeed(FeedUserRequest request) throws NetworkRequestException {
		return blockedUsersWrapper.getResponse(request);
	}

	@Override
	public UsersListResponse getUserFollowerFeed(GetFollowFeedRequest request) throws NetworkRequestException {
		return userFollowerFeedWrapper.getResponse(request);
	}

	@Override
	public UsersListResponse getUserFollowingFeed(GetFollowFeedRequest request) throws NetworkRequestException {
		return userFollowingWrapper.getResponse(request);
	}

	@Override
	public UsersListResponse getUserPendingFeed(FeedUserRequest request) throws NetworkRequestException {
		return pendingUsersWrapper.getResponse(request);
	}

	@Override
	public Response rejectUser(RejectFollowRequest request) throws NetworkRequestException {
		return wrappedService.rejectUser(request);
	}

	@Override
	public Response unblockUser(UnblockUserRequest request) throws NetworkRequestException {
		return wrappedService.unblockUser(request);
	}

	@Override
	public Response unfollowUser(UnfollowUserRequest request) throws NetworkRequestException {
		return wrappedService.unfollowUser(request);
	}
}
