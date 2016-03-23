/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.fetcher;

import com.microsoft.socialplus.autorest.models.ContentType;
import com.microsoft.socialplus.autorest.models.IdentityProvider;
import com.microsoft.socialplus.base.GlobalObjectRegistry;
import com.microsoft.socialplus.base.function.Producer;
import com.microsoft.socialplus.data.model.AccountData;
import com.microsoft.socialplus.data.model.CommentFeedType;
import com.microsoft.socialplus.data.model.FollowRequest;
import com.microsoft.socialplus.data.model.TopicFeedType;
import com.microsoft.socialplus.fetcher.base.DataState;
import com.microsoft.socialplus.fetcher.base.Fetcher;
import com.microsoft.socialplus.fetcher.base.RequestType;
import com.microsoft.socialplus.server.IActivityService;
import com.microsoft.socialplus.server.IContentService;
import com.microsoft.socialplus.server.INotificationService;
import com.microsoft.socialplus.server.IRelationshipService;
import com.microsoft.socialplus.server.ISearchService;
import com.microsoft.socialplus.server.ServerMethod;
import com.microsoft.socialplus.server.SocialPlusServiceProvider;
import com.microsoft.socialplus.server.model.BaseRequest;
import com.microsoft.socialplus.server.model.FeedUserRequest;
import com.microsoft.socialplus.server.model.ListResponse;
import com.microsoft.socialplus.server.model.content.topics.GetTopicFeedRequest;
import com.microsoft.socialplus.server.model.like.GetLikeFeedRequest;
import com.microsoft.socialplus.server.model.pin.GetPinFeedRequest;
import com.microsoft.socialplus.server.model.relationship.GetBlockedUsersRequest;
import com.microsoft.socialplus.server.model.relationship.GetFollowerFeedRequest;
import com.microsoft.socialplus.server.model.relationship.GetFollowingFeedRequest;
import com.microsoft.socialplus.server.model.search.GetPopularUsersRequest;
import com.microsoft.socialplus.server.model.search.GetTrendingHashtagsRequest;
import com.microsoft.socialplus.server.model.search.SearchTopicsRequest;
import com.microsoft.socialplus.server.model.search.SearchUsersRequest;
import com.microsoft.socialplus.server.model.view.ActivityView;
import com.microsoft.socialplus.server.model.view.ActivityViewAssertion;
import com.microsoft.socialplus.server.model.view.CommentView;
import com.microsoft.socialplus.server.model.view.TopicView;
import com.microsoft.socialplus.server.model.view.UserCompactView;
import com.microsoft.socialplus.social.AuthorizationRequest;

import java.util.Collections;
import java.util.List;

/**
 * Produces {@link Fetcher} instances.
 */
public final class FetchersFactory {

	private static final IRelationshipService RELATIONSHIP_SERVICE;
	private static final IContentService CONTENT_SERVICE;
	private static final ISearchService SEARCH_SERVICE;
	private static final INotificationService NOTIFICATION_SERVICE;
	private static final IActivityService ACTIVITY_SERVICE;

	static {
		SocialPlusServiceProvider socialPlusServiceProvider = GlobalObjectRegistry.getObject(SocialPlusServiceProvider.class);
		RELATIONSHIP_SERVICE = socialPlusServiceProvider.getRelationshipService();
		CONTENT_SERVICE = socialPlusServiceProvider.getContentService();
		SEARCH_SERVICE = socialPlusServiceProvider.getSearchService();
		NOTIFICATION_SERVICE = socialPlusServiceProvider.getNotificationService();
		ACTIVITY_SERVICE = socialPlusServiceProvider.getActivityService();
	}

	public static Fetcher<UserCompactView> createFollowersFetcher(String userHandle) {
		return createBatchFetcher(RELATIONSHIP_SERVICE::getUserFollowerFeed, () -> new GetFollowerFeedRequest(userHandle));
	}

	public static Fetcher<UserCompactView> createFollowingFetcher(String userHandle) {
		return createBatchFetcher(RELATIONSHIP_SERVICE::getUserFollowingFeed, () -> new GetFollowingFeedRequest(userHandle));
	}

	public static Fetcher<UserCompactView> createBlockedUsersFetcher() {
		return createBatchFetcher(RELATIONSHIP_SERVICE::getUserBlockedFeed, () -> new GetBlockedUsersRequest());
	}

	public static Fetcher<Object> createCommentFeedFetcher(String topicHandle, TopicView topicView) {
		return new CommentFeedFetcher(CommentFeedType.RECENT, topicHandle, topicView);
	}

	public static Fetcher<Object> createReplyFeedFetcher(String commentHandle, CommentView commentView) {
		return new ReplyFeedFetcher(commentHandle, commentView);
	}

	public static Fetcher<String> createTrendingHashtagsFetcher() {
		return createFetcher(SEARCH_SERVICE::getTrendingHashtags, GetTrendingHashtagsRequest::new);
	}

	public static Fetcher<UserCompactView> createLikeFeedFetcher(String contentHandle, ContentType contentType) {
		return createBatchFetcher(CONTENT_SERVICE::getLikeFeed, () -> new GetLikeFeedRequest(contentHandle, contentType));
	}

	public static Fetcher<ActivityView> createNotificationFeedFetcher() {
		return new NotificationFetcher(NOTIFICATION_SERVICE::getNotificationFeed, ActivityViewAssertion.forUserFeed());
	}

	public static Fetcher<ActivityView> createFollowingActivityFeedFetcher() {
		return new RecentActivityFetcher(ACTIVITY_SERVICE::getFollowingActivityFeed, ActivityViewAssertion.forFollowingFeed());
	}

	public static Fetcher<FollowRequest> createFollowRequestFetcher() {
		return new FollowRequestsFetcher();
	}

	public static Fetcher<TopicView> createTopicFeedFetcher(TopicFeedType topicFeedType) {
		return createBatchFetcher(CONTENT_SERVICE::getTopicFeed, () -> new GetTopicFeedRequest(topicFeedType));
	}

	public static Fetcher<TopicView> createPinsFeedFetcher() {
		return createBatchFetcher(CONTENT_SERVICE::getPinFeed, () -> new GetPinFeedRequest());
	}

	public static Fetcher<TopicView> createProfileFeedFetcher(String userHandle, TopicFeedType topicFeedType) {
		return createBatchFetcher(CONTENT_SERVICE::getTopicFeed, () -> new GetTopicFeedRequest(userHandle, topicFeedType));
	}

	public static Fetcher<AccountData> createProfileFetcher(String userHandle) {
		return new ProfileFetcher(userHandle);
	}

	public static Fetcher<UserCompactView> createPopularUsersFetcher() {
		return createBatchFetcher(SEARCH_SERVICE::getPopularUsers, () -> new GetPopularUsersRequest());
	}

	public static Fetcher<UserCompactView> createSearchUsersFetcher(String query) {
		return createBatchFetcher(SEARCH_SERVICE::searchUsers, () -> new SearchUsersRequest(query));
	}

	public static Fetcher<TopicView> createSearchTopicsFetcher(String query) {
		return createBatchFetcher(SEARCH_SERVICE::searchTopics, () -> new SearchTopicsRequest(query));
	}

	public static Fetcher<UserCompactView> createFriendlistFetcher(IdentityProvider identityProvider, AuthorizationRequest authorizationRequest) {
		return new FriendlistFetcher(SEARCH_SERVICE, identityProvider, authorizationRequest);
	}

	public static <T> Fetcher<T> createDummyFetcher() {
		return new Fetcher<T>() {
			@Override
			protected List<T> fetchDataPage(DataState dataState, RequestType requestType, int pageSize) throws Exception {
				dataState.markDataEnded();
				return Collections.emptyList();
			}
		};
	}

	private static <T, R extends BaseRequest> Fetcher<T> createFetcher(ServerMethod<R, ListResponse<T>> serverMethod, Producer<R> requestProducer) {
		return new ServerDataFetcher<>(new DataRequestExecutor<>(serverMethod, requestProducer));
	}

	private static <T, R extends FeedUserRequest> Fetcher<T> createBatchFetcher(ServerMethod<R, ListResponse<T>> serverMethod, Producer<R> requestProducer) {
		return new ServerDataFetcher<>(new BatchDataRequestExecutor<>(serverMethod, requestProducer));
	}

	private static <T> Fetcher<T> createBatchFetcher(ServerMethod<FeedUserRequest, ListResponse<T>> serverMethod) {
		return new ServerDataFetcher<>(new BatchDataRequestExecutor<>(serverMethod, FeedUserRequest::new));
	}

	private FetchersFactory() {
	}
}
