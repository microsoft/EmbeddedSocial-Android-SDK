/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.data.storage;

import android.content.Context;

import com.microsoft.socialplus.data.storage.request.wrapper.content.CommentFeedRequestWrapper;
import com.microsoft.socialplus.data.storage.request.wrapper.content.CommentRequestWrapper;
import com.microsoft.socialplus.data.storage.request.wrapper.content.LikeFeedRequestWrapper;
import com.microsoft.socialplus.data.storage.request.wrapper.content.PinFeedRequestWrapper;
import com.microsoft.socialplus.data.storage.request.wrapper.content.ReplyFeedRequestWrapper;
import com.microsoft.socialplus.data.storage.request.wrapper.content.ReplyRequestWrapper;
import com.microsoft.socialplus.data.storage.request.wrapper.content.TopicFeedRequestWrapper;
import com.microsoft.socialplus.data.storage.request.wrapper.content.TopicRequestWrapper;
import com.microsoft.socialplus.server.IContentService;
import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.microsoft.socialplus.server.model.FeedUserRequest;
import com.microsoft.socialplus.server.model.UsersListResponse;
import com.microsoft.socialplus.server.model.content.comments.AddCommentRequest;
import com.microsoft.socialplus.server.model.content.comments.AddCommentResponse;
import com.microsoft.socialplus.server.model.content.comments.GetCommentFeedRequest;
import com.microsoft.socialplus.server.model.content.comments.GetCommentFeedResponse;
import com.microsoft.socialplus.server.model.content.comments.GetCommentRequest;
import com.microsoft.socialplus.server.model.content.comments.GetCommentResponse;
import com.microsoft.socialplus.server.model.content.comments.RemoveCommentRequest;
import com.microsoft.socialplus.server.model.content.replies.AddReplyRequest;
import com.microsoft.socialplus.server.model.content.replies.AddReplyResponse;
import com.microsoft.socialplus.server.model.content.replies.GetReplyFeedRequest;
import com.microsoft.socialplus.server.model.content.replies.GetReplyFeedResponse;
import com.microsoft.socialplus.server.model.content.replies.GetReplyRequest;
import com.microsoft.socialplus.server.model.content.replies.GetReplyResponse;
import com.microsoft.socialplus.server.model.content.replies.RemoveReplyRequest;
import com.microsoft.socialplus.server.model.content.topics.AddTopicRequest;
import com.microsoft.socialplus.server.model.content.topics.AddTopicResponse;
import com.microsoft.socialplus.server.model.content.topics.GetTopicFeedRequest;
import com.microsoft.socialplus.server.model.content.topics.GetTopicRequest;
import com.microsoft.socialplus.server.model.content.topics.GetTopicResponse;
import com.microsoft.socialplus.server.model.content.topics.HideTopicRequest;
import com.microsoft.socialplus.server.model.content.topics.RemoveTopicRequest;
import com.microsoft.socialplus.server.model.content.topics.TopicsListResponse;
import com.microsoft.socialplus.server.model.content.topics.UpdateTopicRequest;
import com.microsoft.socialplus.server.model.like.AddLikeRequest;
import com.microsoft.socialplus.server.model.like.GetLikeFeedRequest;
import com.microsoft.socialplus.server.model.like.RemoveLikeRequest;
import com.microsoft.socialplus.server.model.pin.AddPinRequest;
import com.microsoft.socialplus.server.model.pin.GetPinFeedRequest;
import com.microsoft.socialplus.server.model.pin.RemovePinRequest;

import retrofit2.Response;

/**
 * Provides transparent cache implementation of top of {@linkplain IContentService}.
 */
public class ContentServiceCachingWrapper implements IContentService {

	private final TopicFeedRequestWrapper topicFeedRequestWrapper;
	private final ReplyFeedRequestWrapper replyFeedRequestWrapper;
	private final CommentFeedRequestWrapper commentFeedRequestWrapper;
	private final PinFeedRequestWrapper pinRequestWrapper;
	private final LikeFeedRequestWrapper likeFeedRequestWrapper;
	private final TopicRequestWrapper topicRequestWrapper;
	private final ReplyRequestWrapper replyRequestWrapper;
	private final CommentRequestWrapper commentRequestWrapper;

	/**
	 * Creates an instance.
	 */
	public ContentServiceCachingWrapper(Context context) {
		TopicCache topicCache = new TopicCache();
		PostStorage postStorage = new PostStorage(context);
		commentRequestWrapper = new CommentRequestWrapper(this::getComment, topicCache);
		replyRequestWrapper = new ReplyRequestWrapper(this::getReply, topicCache);
		topicFeedRequestWrapper = new TopicFeedRequestWrapper(this::getTopicFeed,
			postStorage, topicCache);
		topicRequestWrapper = new TopicRequestWrapper(this::getTopic, topicCache);
		likeFeedRequestWrapper = new LikeFeedRequestWrapper(this::getLikeFeed, new UserCache());
		pinRequestWrapper = new PinFeedRequestWrapper(this::getPinFeed, topicCache);
		commentFeedRequestWrapper = new CommentFeedRequestWrapper(this::getCommentFeed,
			topicCache, context);
		replyFeedRequestWrapper = new ReplyFeedRequestWrapper(this::getReplyFeed,
			topicCache, context);
	}

	@Override
	public AddCommentResponse addComment(AddCommentRequest request) throws NetworkRequestException {
		return request.send();
	}

	@Override
	public Response addLike(AddLikeRequest request) throws NetworkRequestException {
		return request.send();
	}

	@Override
	public Response addPin(AddPinRequest request) throws NetworkRequestException {
		return request.send();
	}

	@Override
	public AddReplyResponse addReply(AddReplyRequest request) throws NetworkRequestException {
		return request.send();
	}

	@Override
	public AddTopicResponse addTopic(AddTopicRequest request) throws NetworkRequestException {
		return request.send();
	}

	@Override
	public GetCommentResponse getComment(GetCommentRequest request) throws NetworkRequestException {
		return commentRequestWrapper.getResponse(request);
	}

	@Override
	public GetCommentFeedResponse getCommentFeed(GetCommentFeedRequest request) throws NetworkRequestException {
		return commentFeedRequestWrapper.getResponse(request);
	}

	@Override
	public UsersListResponse getLikeFeed(GetLikeFeedRequest request) throws NetworkRequestException {
		return likeFeedRequestWrapper.getResponse(request);
	}

	@Override
	public TopicsListResponse getPinFeed(GetPinFeedRequest request) throws NetworkRequestException {
		return pinRequestWrapper.getResponse(request);
	}

	@Override
	public GetReplyResponse getReply(GetReplyRequest request) throws NetworkRequestException {
		return replyRequestWrapper.getResponse(request);
	}

	@Override
	public GetReplyFeedResponse getReplyFeed(GetReplyFeedRequest request) throws NetworkRequestException {
		return replyFeedRequestWrapper.getResponse(request);
	}

	@Override
	public GetTopicResponse getTopic(GetTopicRequest request) throws NetworkRequestException {
		return topicRequestWrapper.getResponse(request);
	}

	@Override
	public TopicsListResponse getTopicFeed(GetTopicFeedRequest request) throws NetworkRequestException {
		return topicFeedRequestWrapper.getResponse(request);
	}

	@Override
	public Response removeComment(RemoveCommentRequest request) throws NetworkRequestException {
		return request.send();
	}

	@Override
	public Response removeLike(RemoveLikeRequest request) throws NetworkRequestException {
		return request.send();
	}

	@Override
	public Response removePin(RemovePinRequest request) throws NetworkRequestException {
		return request.send();
	}

	@Override
	public Response hideTopic(HideTopicRequest request) throws NetworkRequestException {
		return request.send();
	}

	@Override
	public Response removeReply(RemoveReplyRequest request) throws NetworkRequestException {
		return request.send();
	}

	@Override
	public Response removeTopic(RemoveTopicRequest request) throws NetworkRequestException {
		return request.send();
	}

	@Override
	public Response updateTopic(UpdateTopicRequest request) throws NetworkRequestException {
		return request.send();
	}
}
