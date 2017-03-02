/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.fetcher;

import com.microsoft.embeddedsocial.autorest.models.PublisherType;
import com.microsoft.embeddedsocial.data.model.AccountData;
import com.microsoft.embeddedsocial.fetcher.base.RequestType;
import com.microsoft.embeddedsocial.server.INotificationService;
import com.microsoft.embeddedsocial.server.ServerMethod;
import com.microsoft.embeddedsocial.server.model.BaseRequest;
import com.microsoft.embeddedsocial.server.model.ListResponse;
import com.microsoft.embeddedsocial.server.model.like.GetLikeFeedRequest;
import com.microsoft.embeddedsocial.server.model.relationship.GetFollowerFeedRequest;
import com.microsoft.embeddedsocial.server.model.relationship.GetFollowingFeedRequest;
import com.microsoft.embeddedsocial.server.model.relationship.GetMyFollowerFeedRequest;
import com.microsoft.embeddedsocial.server.model.relationship.GetMyFollowingUsersFeedRequest;
import com.microsoft.embeddedsocial.server.model.view.ActivityView;
import com.microsoft.embeddedsocial.server.model.view.UserCompactView;
import com.microsoft.embeddedsocial.social.AuthorizationRequest;
import com.microsoft.embeddedsocial.autorest.models.ContentType;
import com.microsoft.embeddedsocial.autorest.models.IdentityProvider;
import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.base.function.Producer;
import com.microsoft.embeddedsocial.data.model.CommentFeedType;
import com.microsoft.embeddedsocial.data.model.FollowRequest;
import com.microsoft.embeddedsocial.data.model.TopicFeedType;
import com.microsoft.embeddedsocial.fetcher.base.DataState;
import com.microsoft.embeddedsocial.fetcher.base.Fetcher;
import com.microsoft.embeddedsocial.server.IActivityService;
import com.microsoft.embeddedsocial.server.IContentService;
import com.microsoft.embeddedsocial.server.IRelationshipService;
import com.microsoft.embeddedsocial.server.ISearchService;
import com.microsoft.embeddedsocial.server.EmbeddedSocialServiceProvider;
import com.microsoft.embeddedsocial.server.model.FeedUserRequest;
import com.microsoft.embeddedsocial.server.model.content.topics.GetTopicFeedRequest;
import com.microsoft.embeddedsocial.server.model.pin.GetPinFeedRequest;
import com.microsoft.embeddedsocial.server.model.relationship.GetBlockedUsersRequest;
import com.microsoft.embeddedsocial.server.model.relationship.GetFollowingInOtherAppsRequest;
import com.microsoft.embeddedsocial.server.model.search.GetPopularUsersRequest;
import com.microsoft.embeddedsocial.server.model.search.GetTrendingHashtagsRequest;
import com.microsoft.embeddedsocial.server.model.search.SearchTopicsRequest;
import com.microsoft.embeddedsocial.server.model.search.SearchUsersRequest;
import com.microsoft.embeddedsocial.server.model.view.ActivityViewAssertion;
import com.microsoft.embeddedsocial.server.model.view.CommentView;
import com.microsoft.embeddedsocial.server.model.view.TopicView;

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
		EmbeddedSocialServiceProvider embeddedSocialServiceProvider = GlobalObjectRegistry.getObject(EmbeddedSocialServiceProvider.class);
		RELATIONSHIP_SERVICE = embeddedSocialServiceProvider.getRelationshipService();
		CONTENT_SERVICE = embeddedSocialServiceProvider.getContentService();
		SEARCH_SERVICE = embeddedSocialServiceProvider.getSearchService();
		NOTIFICATION_SERVICE = embeddedSocialServiceProvider.getNotificationService();
		ACTIVITY_SERVICE = embeddedSocialServiceProvider.getActivityService();
	}

	public static Fetcher<UserCompactView> createFollowersFetcher(String userHandle) {
		return createBatchFetcher(RELATIONSHIP_SERVICE::getUserFollowerFeed, () -> new GetFollowerFeedRequest(userHandle));
	}

	public static Fetcher<UserCompactView> createFollowingFetcher(String userHandle) {
		return createBatchFetcher(RELATIONSHIP_SERVICE::getUserFollowingFeed, () -> new GetFollowingFeedRequest(userHandle));
	}

	public static Fetcher<UserCompactView> createMyFollowersFetcher(String userHandle) {
		return createBatchFetcher(RELATIONSHIP_SERVICE::getMyFollowerFeed, () -> new GetMyFollowerFeedRequest());
	}

	public static Fetcher<UserCompactView> createMyFollowingFetcher(String userHandle) {
		return createBatchFetcher(RELATIONSHIP_SERVICE::getMyFollowingUsersFeed, () -> new GetMyFollowingUsersFeedRequest());
	}

	public static Fetcher<UserCompactView> createMyFollowingInOtherAppsFetcher(String userHandle) {
		return createBatchFetcher(RELATIONSHIP_SERVICE::getUserFollowingInOtherAppsFeed, () -> new GetFollowingInOtherAppsRequest());
	}

	public static Fetcher<UserCompactView> createBlockedUsersFetcher() {
		return createBatchFetcher(RELATIONSHIP_SERVICE::getUserBlockedFeed, () -> new GetBlockedUsersRequest());
	}

	public static Fetcher<Object> createCommentFeedFetcher(String topicHandle, TopicView topicView) {
		return new CommentFeedFetcher(CommentFeedType.RECENT, topicHandle, topicView);
	}

	public static Fetcher<Object> createCommentFeedFetcherFromTopicName(String topicName, PublisherType publisherType) {
		return new CommentFeedFetcherFromTopicName(CommentFeedType.RECENT, topicName, publisherType);
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
