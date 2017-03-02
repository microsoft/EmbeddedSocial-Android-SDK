/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.test.embeddedsocial.test;

import com.microsoft.embeddedsocial.server.ISearchService;
import com.microsoft.embeddedsocial.server.model.UsersListResponse;
import com.microsoft.embeddedsocial.server.model.auth.AuthenticationResponse;
import com.microsoft.embeddedsocial.server.model.content.topics.TopicsListResponse;
import com.microsoft.embeddedsocial.server.model.search.GetTrendingHashtagsRequest;
import com.microsoft.embeddedsocial.server.model.search.GetTrendingHashtagsResponse;
import com.microsoft.embeddedsocial.server.model.search.SearchTopicsRequest;
import com.microsoft.embeddedsocial.server.model.search.SearchUsersRequest;
import com.microsoft.embeddedsocial.server.model.view.TopicView;
import com.microsoft.embeddedsocial.server.model.view.UserCompactView;
import com.microsoft.test.embeddedsocial.TestConstants;

import java.util.List;

public class SearchTest extends BaseRestServicesTest {

	private ISearchService searchService;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		searchService = getServiceProvider().getSearchService();
	}

	public void testTrendingHashtags() throws Exception {
		AuthenticationResponse authenticationResponse = createRandomUser();
		GetTrendingHashtagsResponse trendingHashtagsResponse
			= searchService.getTrendingHashtags(prepareUserRequest(new GetTrendingHashtagsRequest(), authenticationResponse));
		List<String> hashtags = trendingHashtagsResponse.getData();
		assertNotNull(hashtags);
	}

	public void testSearchTopics() throws Exception {
		AuthenticationResponse authenticationResponse = createRandomUser();
		String topicHandle = addTopic(authenticationResponse,
			TestConstants.TOPIC_TITLE,
			TestConstants.HASHTAG);
		delay();
		try {
			SearchTopicsRequest searchTopicsRequest = new SearchTopicsRequest(TestConstants.HASHTAG);
			TopicsListResponse searchTopicsResponse
				= searchService.searchTopics(prepareUserRequest(searchTopicsRequest, authenticationResponse));
			List<TopicView> topics = searchTopicsResponse.getData();
			assertNotNull(topics);
			assertTrue(topics.size() > 0);
		} finally {
			try {
				removeTopic(authenticationResponse, topicHandle);
			} catch (Exception e) {
				//ignore
			}
		}
	}

	public void testSearchUsers() throws Exception {
		AuthenticationResponse authenticationResponse = createRandomUser();
		SearchUsersRequest searchUsersRequest = new SearchUsersRequest(TestConstants.NAME);
		UsersListResponse searchUsersResponse
			= searchService.searchUsers(prepareUserRequest(searchUsersRequest, authenticationResponse));
		List<UserCompactView> users = searchUsersResponse.getData();
		assertNotNull(users);
		assertTrue(users.size() > 0);
	}
}
