/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server;

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
 * Interface for management relationship between users
 */
public interface IRelationshipService {

	Response acceptUser(AcceptFollowRequest request)
			throws NetworkRequestException;

	Response blockUser(BlockUserRequest request)
			throws NetworkRequestException;

	FollowUserResponse followUser(FollowUserRequest request)
			throws NetworkRequestException;

	UsersListResponse getUserBlockedFeed(FeedUserRequest request)
			throws NetworkRequestException;

	UsersListResponse getUserFollowerFeed(GetFollowFeedRequest request)
			throws NetworkRequestException;

	UsersListResponse getUserFollowingFeed(GetFollowFeedRequest request)
			throws NetworkRequestException;

	UsersListResponse getUserPendingFeed(FeedUserRequest request)
			throws NetworkRequestException;

	Response rejectUser(RejectFollowRequest request)
			throws NetworkRequestException;

	Response unblockUser(UnblockUserRequest request)
			throws NetworkRequestException;

	Response unfollowUser(UnfollowUserRequest request)
			throws NetworkRequestException;
}
