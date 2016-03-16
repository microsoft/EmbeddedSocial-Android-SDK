/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.test.socialplus.test;

import com.microsoft.autorest.models.Visibility;
import com.microsoft.socialplus.server.IRelationshipService;
import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.microsoft.socialplus.server.model.UsersListResponse;
import com.microsoft.socialplus.server.model.account.UpdateUserVisibilityRequest;
import com.microsoft.socialplus.server.model.auth.AuthenticationResponse;
import com.microsoft.socialplus.server.model.relationship.AcceptFollowRequest;
import com.microsoft.socialplus.server.model.relationship.BlockUserRequest;
import com.microsoft.socialplus.server.model.relationship.FollowUserRequest;
import com.microsoft.socialplus.server.model.relationship.FollowUserResponse;
import com.microsoft.socialplus.server.model.relationship.GetBlockedUsersRequest;
import com.microsoft.socialplus.server.model.relationship.GetFollowerFeedRequest;
import com.microsoft.socialplus.server.model.relationship.GetFollowingFeedRequest;
import com.microsoft.socialplus.server.model.relationship.GetPendingUsersRequest;
import com.microsoft.socialplus.server.model.relationship.RejectFollowRequest;
import com.microsoft.socialplus.server.model.relationship.UnblockUserRequest;
import com.microsoft.socialplus.server.model.relationship.UnfollowUserRequest;
import com.microsoft.socialplus.server.model.view.UserCompactView;

import java.util.List;

public class UserRelationshipTest extends BaseRestServicesTest {

	private IRelationshipService relationshipService;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		relationshipService = getServiceProvider().getRelationshipService();
	}

	public void testAcceptUser() throws Exception {

		new UserRelationshipOperationExecutor() {

			@Override
			protected void onExecute() throws NetworkRequestException {
				FollowUserRequest followRequest
						= new FollowUserRequest(getSecondUser().getUserHandle());
				FollowUserResponse followUserResponse
						= relationshipService.followUser(prepareUserRequest(followRequest, getFirstUser()));
				AcceptFollowRequest acceptRequest = new AcceptFollowRequest(getFirstUser().getUserHandle());
				relationshipService.acceptUser(prepareUserRequest(acceptRequest, getSecondUser()));

				delay();

				GetFollowerFeedRequest getFollowerFeedRequest = new GetFollowerFeedRequest(getSecondUser().getUserHandle());
				UsersListResponse userFollowerFeedResponse
						= relationshipService.getUserFollowerFeed(prepareUserRequest(getFollowerFeedRequest, getSecondUser()));
				List<UserCompactView> followers = userFollowerFeedResponse.getData();
				assertNotNull(followers);
				assertTrue(followers.size() == 1);
				assertEquals(getFirstUser().getUserHandle(), userFollowerFeedResponse.getData().get(0).getHandle());
				GetFollowingFeedRequest getFollowingFeedRequest = new GetFollowingFeedRequest(getFirstUser().getUserHandle());
				UsersListResponse userFollowingFeedResponse
						= relationshipService.getUserFollowingFeed(prepareUserRequest(getFollowingFeedRequest, getFirstUser()));
				List<UserCompactView> following = userFollowingFeedResponse.getData();
				assertNotNull(following);
				assertTrue(following.size() == 1);
				assertEquals(getSecondUser().getUserHandle(), userFollowingFeedResponse.getData().get(0).getHandle());

			}
		}.execute();
	}

	public void testRejectUser() throws Exception {
		new UserRelationshipOperationExecutor() {

			@Override
			protected void onExecute() throws NetworkRequestException {
				FollowUserRequest followRequest
						= new FollowUserRequest(getSecondUser().getUserHandle());
				FollowUserResponse followUserResponse
						= relationshipService.followUser(prepareUserRequest(followRequest, getFirstUser()));
				RejectFollowRequest rejectRequest
						= new RejectFollowRequest(getFirstUser().getUserHandle());
				relationshipService.rejectUser(prepareUserRequest(rejectRequest, getSecondUser()));

				delay();

				GetFollowerFeedRequest getFollowerFeedRequest = new GetFollowerFeedRequest(getSecondUser().getUserHandle());
				UsersListResponse userFollowerFeedResponse
						= relationshipService.getUserFollowerFeed(prepareUserRequest(getFollowerFeedRequest, getSecondUser()));

				assertNotNull(userFollowerFeedResponse.getData());
				assertTrue(userFollowerFeedResponse.getData().size() == 0);

				GetFollowingFeedRequest getFollowingFeedRequest = new GetFollowingFeedRequest(getFirstUser().getUserHandle());
				UsersListResponse userFollowingFeedResponse
						= relationshipService.getUserFollowingFeed(prepareUserRequest(getFollowingFeedRequest, getFirstUser()));

				assertNotNull(userFollowingFeedResponse);
				assertTrue(userFollowingFeedResponse.getData().size() == 0);
			}
		}.execute();
	}

	public void testBlock() throws Exception {
		new UserRelationshipOperationExecutor() {

			@Override
			protected void onExecute() throws NetworkRequestException {

				BlockUserRequest blockRequest
						= new BlockUserRequest(getSecondUser().getUserHandle());
				relationshipService.blockUser(prepareUserRequest(blockRequest, getFirstUser()));

				delay();

				GetBlockedUsersRequest getBlockFeedRequest = new GetBlockedUsersRequest();
				UsersListResponse userBlockedFeedResponse
						= relationshipService.getUserBlockedFeed(prepareUserRequest(getBlockFeedRequest, getFirstUser()));
				assertNotNull(userBlockedFeedResponse.getData());
				assertEquals(getSecondUser().getUserHandle(),
						userBlockedFeedResponse.getData().get(0).getHandle());

				UnblockUserRequest unblockRequest
						= new UnblockUserRequest(getSecondUser().getUserHandle());
				relationshipService.unblockUser(prepareUserRequest(unblockRequest, getFirstUser()));
			}
		}.execute();
	}

	public void testPending() throws Exception {
		new UserRelationshipOperationExecutor() {

			@Override
			protected void onExecute() throws NetworkRequestException {

				UpdateUserVisibilityRequest updateUserVisibilityRequest
						= new UpdateUserVisibilityRequest(Visibility.PRIVATE);

				getServiceProvider().getAccountService()
						.updateUserVisibility(prepareUserRequest(updateUserVisibilityRequest, getSecondUser()));

				FollowUserRequest followRequest
						= new FollowUserRequest(getSecondUser().getUserHandle());
				relationshipService.followUser(prepareUserRequest(followRequest, getFirstUser()));

				delay();

				GetPendingUsersRequest getPendingFeedRequest = new GetPendingUsersRequest();
				UsersListResponse userPendingFeedResponse
						= relationshipService.getUserPendingFeed(prepareUserRequest(getPendingFeedRequest, getSecondUser()));
				List<UserCompactView> users = userPendingFeedResponse.getData();
				assertTrue(users.size() == 1);
				assertEquals(getFirstUser().getUserHandle(), users.get(0).getHandle());
			}
		}.execute();
	}

	public void testUnfollow() throws Exception {
		new UserRelationshipOperationExecutor() {

			@Override
			protected void onExecute() throws NetworkRequestException {
				FollowUserRequest followRequest
						= new FollowUserRequest(getSecondUser().getUserHandle());
				relationshipService.followUser(prepareUserRequest(followRequest, getFirstUser()));

				delay();

				GetFollowerFeedRequest getFollowerFeedRequest = new GetFollowerFeedRequest(getSecondUser().getUserHandle());
				GetFollowingFeedRequest getFollowingFeedRequest = new GetFollowingFeedRequest(getFirstUser().getUserHandle());

				UsersListResponse userFollowerFeed
						= relationshipService.getUserFollowerFeed(prepareUserRequest(getFollowerFeedRequest, getFirstUser()));
				assertTrue(userFollowerFeed.getData().size() == 1);

				UsersListResponse userFollowingFeed
						= relationshipService.getUserFollowingFeed(prepareUserRequest(getFollowingFeedRequest, getFirstUser()));
				assertTrue(userFollowingFeed.getData().size() == 1);

				UnfollowUserRequest unfollowUserRequest
						= new UnfollowUserRequest(getSecondUser().getUserHandle());
				relationshipService.unfollowUser(unfollowUserRequest);

				delay();

				userFollowerFeed = relationshipService
						.getUserFollowerFeed(prepareUserRequest(getFollowerFeedRequest, getFirstUser()));
				assertTrue(userFollowerFeed.getData().size() == 0);

				userFollowingFeed = relationshipService
						.getUserFollowingFeed(prepareUserRequest(getFollowingFeedRequest, getFirstUser()));
				assertTrue(userFollowingFeed.getData().size() == 0);

			}
		}.execute();
	}

	private abstract class UserRelationshipOperationExecutor {

		private AuthenticationResponse user1;
		private AuthenticationResponse user2;

		protected AuthenticationResponse getFirstUser() {
			return user1;
		}

		protected AuthenticationResponse getSecondUser() {
			return user2;
		}

		public final void execute() throws NetworkRequestException {
			user1 = createRandomUser();
			user2 = createRandomUser();
			try {
				onExecute();
			} finally {
				try {
					deleteUser(user1);
					deleteUser(user2);
				} catch (Exception e) {
					//ignoring
				}
			}
		}

		protected abstract void onExecute() throws NetworkRequestException;
	}
}
