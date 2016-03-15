/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.test.socialplus.test;

import com.microsoft.socialplus.server.IActivityService;
import com.microsoft.socialplus.server.IRelationshipService;
import com.microsoft.socialplus.server.model.FeedUserRequest;
import com.microsoft.socialplus.server.model.activity.ActivityFeedRequest;
import com.microsoft.socialplus.server.model.activity.ActivityFeedResponse;
import com.microsoft.socialplus.server.model.auth.AuthenticationResponse;
import com.microsoft.socialplus.server.model.relationship.AcceptFollowRequest;
import com.microsoft.socialplus.server.model.relationship.FollowUserRequest;

public class ActivityTest extends BaseRestServicesTest {

	private IActivityService activityService;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		activityService = getServiceProvider().getActivityService();
	}

	public void testGetFollowingActivityFeed() throws Exception {
		//TODO ensure this creates 2 separate users
		AuthenticationResponse authenticationResponse = createRandomUser();
		AuthenticationResponse randomUser = createRandomUser();
		String topicHandle = null;

		try {

			IRelationshipService relationshipService = getServiceProvider().getRelationshipService();
			FollowUserRequest followRequest = new FollowUserRequest(randomUser.getUserHandle());
			relationshipService.followUser(prepareUserRequest(followRequest, authenticationResponse));
			AcceptFollowRequest acceptRequest = new AcceptFollowRequest(authenticationResponse.getUserHandle());
			getServiceProvider().getRelationshipService().acceptUser(prepareUserRequest(acceptRequest, randomUser));
			topicHandle = addTopic(randomUser);
			delay();

			ActivityFeedRequest getFollowingActivityFeedRequest = new ActivityFeedRequest();
			ActivityFeedResponse followingActivityFeed = activityService.
					getFollowingActivityFeed(prepareUserRequest(getFollowingActivityFeedRequest, authenticationResponse));
		} finally {
			try {
				if (topicHandle != null) {
					removeTopic(randomUser, topicHandle);
				}
			} catch (Exception e) {
				//ignore
			}
			try {
				deleteUser(randomUser);
			} catch (Exception e) {
				//ignore
			}
		}
	}

}
