/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.content.topics;

import com.microsoft.autorest.UserTopicsOperations;
import com.microsoft.autorest.UserTopicsOperationsImpl;
import com.microsoft.autorest.models.FeedResponseTopicView;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.socialplus.base.utils.EnumUtils;
import com.microsoft.socialplus.data.model.TopicFeedType;
import com.microsoft.socialplus.server.model.FeedUserRequest;

import java.io.IOException;

public final class GetTopicFeedRequest extends FeedUserRequest {

	private static final UserTopicsOperations USER_TOPICS =
			new UserTopicsOperationsImpl(RETROFIT, CLIENT);
	private final TopicFeedType topicFeedType;
	private final String query;

	public GetTopicFeedRequest(TopicFeedType topicFeedType) {
		this(null, topicFeedType);
	}

	public GetTopicFeedRequest(String query, TopicFeedType topicFeedType) {
		this.query = query;
		this.topicFeedType = topicFeedType;
	}

	public String getQuery() {
		return query;
	}

	public TopicFeedType getTopicFeedType() {
		return topicFeedType;
	}

	@Override
	public TopicsListResponse send() throws ServiceException, IOException {
		ServiceResponse<FeedResponseTopicView> serviceResponse;
		String cursor = getCursor();
		int limit = getBatchSize();
		switch (topicFeedType) {
			case USER_RECENT:
				// {userHandle}/topics
				serviceResponse = USER_TOPICS.getTopics(query, cursor, limit, appKey, bearerToken);
				break;
			case FOLLOWING_RECENT:
				// users/me/following/topics
				serviceResponse = FOLLOWING.getTopics(bearerToken, cursor, limit);
				break;
			case EVERYONE_RECENT:
				serviceResponse = TOPICS.getTopics(cursor, limit, appKey, bearerToken);
				break;
			default: // Based on popularity
				serviceResponse = getPopularTopics(cursor, limit);
		}
		return new TopicsListResponse(serviceResponse.getBody());
	}

	private ServiceResponse<FeedResponseTopicView> getPopularTopics(String cursor, int limit)
			throws ServiceException, IOException {
		int intCursor = 0;
		try {
			intCursor = Integer.parseInt(cursor);
		} catch (NumberFormatException e) {
			// TODO
			e.printStackTrace();
		}

		if (topicFeedType == TopicFeedType.USER_POPULAR) {
			// {userHandle}/topics/popular
			return USER_TOPICS.getPopularTopics(query, intCursor, limit, appKey, bearerToken);
		} else {
			String timeRange = getTimeRange();
			return TOPICS.getPopularTopics(timeRange, intCursor, limit, appKey, bearerToken);
		}
	}

	private String getTimeRange() {
		switch (topicFeedType) {
			case EVERYONE_POPULAR_THIS_MONTH:
				return "ThisMonth";
			case EVERYONE_POPULAR_THIS_WEEK:
				return "ThisWeek";
			case EVERYONE_POPULAR_TODAY:
				return "Today";
			default: // all time
				return "AllTime";
		}
	}

	@Override
	public String toString() {
		return "GetTopicFeedRequest{" +
			"query='" + query + '\'' +
			", topicFeedType=" + topicFeedType +
			'}';
	}
}
