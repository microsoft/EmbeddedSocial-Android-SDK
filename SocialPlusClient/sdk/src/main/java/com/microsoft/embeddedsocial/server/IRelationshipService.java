/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server;

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
import com.microsoft.embeddedsocial.server.model.relationship.RejectFollowRequest;
import com.microsoft.embeddedsocial.server.model.relationship.FollowUserRequest;
import com.microsoft.embeddedsocial.server.model.relationship.GetPendingUsersRequest;
import com.microsoft.embeddedsocial.server.model.relationship.RemoveFollowerRequest;
import com.microsoft.embeddedsocial.server.model.relationship.UnblockUserRequest;
import com.microsoft.embeddedsocial.server.model.relationship.UnfollowUserRequest;

import retrofit2.Response;

/**
 * Interface for management relationship between users
 */
public interface IRelationshipService {

	Response acceptUser(AcceptFollowRequest request)
			throws NetworkRequestException;

	Response blockUser(BlockUserRequest request)
			throws NetworkRequestException;

	FollowUserResponse followUser(FollowUserRequest request)
			throws NetworkRequestException;

	UsersListResponse getUserBlockedFeed(GetBlockedUsersRequest request)
			throws NetworkRequestException;

	UsersListResponse getMyFollowerFeed(GetMyFollowerFeedRequest request)
			throws NetworkRequestException;

	UsersListResponse getUserFollowerFeed(GetFollowerFeedRequest request)
			throws NetworkRequestException;

	UsersListResponse getMyFollowingUsersFeed(GetMyFollowingUsersFeedRequest request)
			throws NetworkRequestException;

	UsersListResponse getUserFollowingFeed(GetFollowingFeedRequest request)
			throws NetworkRequestException;

	UsersListResponse getUserPendingFeed(GetPendingUsersRequest request)
			throws NetworkRequestException;

	UsersListResponse getUserFollowingInOtherAppsFeed(GetFollowingInOtherAppsRequest request)
			throws NetworkRequestException;

	Response rejectUser(RejectFollowRequest request)
			throws NetworkRequestException;

	Response unblockUser(UnblockUserRequest request)
			throws NetworkRequestException;

	Response unfollowUser(UnfollowUserRequest request)
			throws NetworkRequestException;

	Response removeFollower(RemoveFollowerRequest request)
		throws NetworkRequestException;
}
