/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage;

import com.microsoft.embeddedsocial.data.storage.request.wrapper.relationship.BlockedUserFeedRequestWrapper;
import com.microsoft.embeddedsocial.data.storage.request.wrapper.relationship.FollowingFeedRequestWrapper;
import com.microsoft.embeddedsocial.data.storage.request.wrapper.relationship.MyFollowerFeedRequestWrapper;
import com.microsoft.embeddedsocial.data.storage.request.wrapper.relationship.MyFollowingInOtherAppsRequestWrapper;
import com.microsoft.embeddedsocial.data.storage.request.wrapper.relationship.PendingUserFeedRequestWrapper;
import com.microsoft.embeddedsocial.server.IRelationshipService;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.UsersListResponse;
import com.microsoft.embeddedsocial.server.model.relationship.AcceptFollowRequest;
import com.microsoft.embeddedsocial.server.model.relationship.BlockUserRequest;
import com.microsoft.embeddedsocial.server.model.relationship.FollowUserResponse;
import com.microsoft.embeddedsocial.server.model.relationship.GetBlockedUsersRequest;
import com.microsoft.embeddedsocial.server.model.relationship.GetFollowerFeedRequest;
import com.microsoft.embeddedsocial.server.model.relationship.GetFollowingFeedRequest;
import com.microsoft.embeddedsocial.server.model.relationship.GetFollowingInOtherAppsRequest;
import com.microsoft.embeddedsocial.server.model.relationship.GetMyFollowerFeedRequest;
import com.microsoft.embeddedsocial.server.model.relationship.GetMyFollowingUsersFeedRequest;
import com.microsoft.embeddedsocial.server.model.relationship.GetPendingUsersRequest;
import com.microsoft.embeddedsocial.server.model.relationship.RejectFollowRequest;
import com.microsoft.embeddedsocial.data.storage.request.wrapper.relationship.FollowerFeedRequestWrapper;
import com.microsoft.embeddedsocial.data.storage.request.wrapper.relationship.MyFollowingFeedRequestWrapper;
import com.microsoft.embeddedsocial.server.model.relationship.FollowUserRequest;
import com.microsoft.embeddedsocial.server.model.relationship.RemoveFollowerRequest;
import com.microsoft.embeddedsocial.server.model.relationship.UnblockUserRequest;
import com.microsoft.embeddedsocial.server.model.relationship.UnfollowUserRequest;

import retrofit2.Response;

/**
 * Provides transparent cache functionality on top of {@linkplain IRelationshipService}.
 */
public class RelationshipServiceCachingWrapper implements IRelationshipService {

	private final BlockedUserFeedRequestWrapper blockedUsersWrapper;
	private final PendingUserFeedRequestWrapper pendingUsersWrapper;
	private final FollowerFeedRequestWrapper userFollowerFeedWrapper;
	private final FollowingFeedRequestWrapper userFollowingWrapper;
	private final MyFollowingFeedRequestWrapper myFollowingWrapper;
	private final MyFollowerFeedRequestWrapper myFollowerWrapper;
	private final MyFollowingInOtherAppsRequestWrapper myFollowingInOtherAppsWrapper;


	/**
	 * Creates an instance.
	 */
	public RelationshipServiceCachingWrapper() {
		UserCache userCache = new UserCache();
		userFollowingWrapper = new FollowingFeedRequestWrapper(
			this::getUserFollowingFeed,
			userCache,
			UserCache.UserFeedType.FOLLOWING
		);
		userFollowerFeedWrapper = new FollowerFeedRequestWrapper(
			this::getUserFollowerFeed,
			userCache,
			UserCache.UserFeedType.FOLLOWER
		);
		blockedUsersWrapper = new BlockedUserFeedRequestWrapper(
			this::getUserBlockedFeed,
			userCache,
			UserCache.UserFeedType.BLOCKED
		);
		pendingUsersWrapper = new PendingUserFeedRequestWrapper(
				this::getUserPendingFeed,
				userCache,
				UserCache.UserFeedType.PENDING
		);
		myFollowingWrapper = new MyFollowingFeedRequestWrapper(
				this::getMyFollowingUsersFeed,
				userCache,
				UserCache.UserFeedType.FOLLOWING
		);
		myFollowerWrapper = new MyFollowerFeedRequestWrapper(
				this::getMyFollowerFeed,
				userCache,
				UserCache.UserFeedType.FOLLOWER
		);
		myFollowingInOtherAppsWrapper = new MyFollowingInOtherAppsRequestWrapper(
				this::getUserFollowingInOtherAppsFeed,
				userCache,
				UserCache.UserFeedType.PENDING
		);
	}

	@Override
	public Response acceptUser(AcceptFollowRequest request) throws NetworkRequestException {
		return request.send();
	}

	@Override
	public Response blockUser(BlockUserRequest request) throws NetworkRequestException {
		return request.send();
	}

	@Override
	public FollowUserResponse followUser(FollowUserRequest request) throws NetworkRequestException {
		return request.send();
	}

	@Override
	public UsersListResponse getUserBlockedFeed(GetBlockedUsersRequest request) throws NetworkRequestException {
		return blockedUsersWrapper.getResponse(request);
	}

	@Override
	public UsersListResponse getUserFollowerFeed(GetFollowerFeedRequest request) throws NetworkRequestException {
		return userFollowerFeedWrapper.getResponse(request);
	}

	@Override
	public UsersListResponse getUserFollowingFeed(GetFollowingFeedRequest request) throws NetworkRequestException {
		return userFollowingWrapper.getResponse(request);
	}

	@Override
	public UsersListResponse getMyFollowerFeed(GetMyFollowerFeedRequest request) throws NetworkRequestException {
		return myFollowerWrapper.getResponse(request);
	}

	@Override
	public UsersListResponse getMyFollowingUsersFeed(GetMyFollowingUsersFeedRequest request) throws NetworkRequestException {
		return myFollowingWrapper.getResponse(request);
	}

	@Override
	public UsersListResponse getUserFollowingInOtherAppsFeed(GetFollowingInOtherAppsRequest request) throws NetworkRequestException {
		return myFollowingInOtherAppsWrapper.getResponse(request);
	}

	@Override
	public UsersListResponse getUserPendingFeed(GetPendingUsersRequest request) throws NetworkRequestException {
		return pendingUsersWrapper.getResponse(request);
	}

	@Override
	public Response rejectUser(RejectFollowRequest request) throws NetworkRequestException {
		return request.send();
	}

	@Override
	public Response unblockUser(UnblockUserRequest request) throws NetworkRequestException {
		return request.send();
	}

	@Override
	public Response unfollowUser(UnfollowUserRequest request) throws NetworkRequestException {
		return request.send();
	}

	@Override
	public Response removeFollower(RemoveFollowerRequest request) throws NetworkRequestException {
		return request.send();
	}
}
