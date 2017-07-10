/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server;

import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.UsersListResponse;
import com.microsoft.embeddedsocial.server.model.content.comments.AddCommentRequest;
import com.microsoft.embeddedsocial.server.model.content.comments.AddCommentResponse;
import com.microsoft.embeddedsocial.server.model.content.comments.GetCommentFeedRequest;
import com.microsoft.embeddedsocial.server.model.content.comments.GetCommentFeedResponse;
import com.microsoft.embeddedsocial.server.model.content.comments.GetCommentRequest;
import com.microsoft.embeddedsocial.server.model.content.comments.GetCommentResponse;
import com.microsoft.embeddedsocial.server.model.content.comments.RemoveCommentRequest;
import com.microsoft.embeddedsocial.server.model.content.replies.AddReplyRequest;
import com.microsoft.embeddedsocial.server.model.content.replies.AddReplyResponse;
import com.microsoft.embeddedsocial.server.model.content.replies.GetReplyFeedResponse;
import com.microsoft.embeddedsocial.server.model.content.replies.GetReplyRequest;
import com.microsoft.embeddedsocial.server.model.content.replies.GetReplyResponse;
import com.microsoft.embeddedsocial.server.model.content.replies.RemoveReplyRequest;
import com.microsoft.embeddedsocial.server.model.content.topics.AddTopicNameRequest;
import com.microsoft.embeddedsocial.server.model.content.topics.AddTopicRequest;
import com.microsoft.embeddedsocial.server.model.content.topics.AddTopicResponse;
import com.microsoft.embeddedsocial.server.model.content.topics.GetTopicFeedRequest;
import com.microsoft.embeddedsocial.server.model.content.topics.GetTopicNameRequest;
import com.microsoft.embeddedsocial.server.model.content.topics.GetTopicRequest;
import com.microsoft.embeddedsocial.server.model.content.topics.GetTopicResponse;
import com.microsoft.embeddedsocial.server.model.content.topics.HideTopicRequest;
import com.microsoft.embeddedsocial.server.model.content.topics.RemoveTopicNameRequest;
import com.microsoft.embeddedsocial.server.model.content.topics.RemoveTopicRequest;
import com.microsoft.embeddedsocial.server.model.content.topics.TopicsListResponse;
import com.microsoft.embeddedsocial.server.model.content.topics.UpdateTopicNameRequest;
import com.microsoft.embeddedsocial.server.model.content.topics.UpdateTopicRequest;
import com.microsoft.embeddedsocial.server.model.like.AddLikeRequest;
import com.microsoft.embeddedsocial.server.model.like.GetLikeFeedRequest;
import com.microsoft.embeddedsocial.server.model.like.RemoveLikeRequest;
import com.microsoft.embeddedsocial.server.model.pin.AddPinRequest;
import com.microsoft.embeddedsocial.server.model.pin.GetPinFeedRequest;
import com.microsoft.embeddedsocial.server.model.pin.RemovePinRequest;
import com.microsoft.embeddedsocial.server.model.content.replies.GetReplyFeedRequest;

import retrofit2.Response;

/**
 * Interface for working with content: topics, replies, comments, images, pins, likes
 */
public interface IContentService {

	// CONTENT

	AddCommentResponse addComment(AddCommentRequest request)
			throws NetworkRequestException;

	AddReplyResponse addReply(AddReplyRequest request)
			throws NetworkRequestException;

	AddTopicResponse addTopic(AddTopicRequest request)
			throws NetworkRequestException;

	GetCommentResponse getComment(GetCommentRequest request)
			throws NetworkRequestException;

	GetCommentFeedResponse getCommentFeed(GetCommentFeedRequest request)
			throws NetworkRequestException;

	GetReplyResponse getReply(GetReplyRequest request)
			throws NetworkRequestException;

	GetReplyFeedResponse getReplyFeed(GetReplyFeedRequest request)
			throws NetworkRequestException;

	GetTopicResponse getTopic(GetTopicRequest request)
			throws NetworkRequestException;

	TopicsListResponse getTopicFeed(GetTopicFeedRequest request)
			throws NetworkRequestException;

	Response removeComment(RemoveCommentRequest request)
			throws NetworkRequestException;

	Response removeReply(RemoveReplyRequest request)
			throws NetworkRequestException;

	Response removeTopic(RemoveTopicRequest request)
			throws NetworkRequestException;

	Response updateTopic(UpdateTopicRequest request)
			throws NetworkRequestException;

	// NAMES
	Response addTopicName(AddTopicNameRequest request)
			throws NetworkRequestException;

	String getTopicName(GetTopicNameRequest request)
			throws NetworkRequestException;

	Response updateTopicName(UpdateTopicNameRequest request)
			throws NetworkRequestException;

	Response removeTopicName(RemoveTopicNameRequest request)
			throws NetworkRequestException;

	// LIKE

	Response addLike(AddLikeRequest request)
			throws NetworkRequestException;

	UsersListResponse getLikeFeed(GetLikeFeedRequest request)
			throws NetworkRequestException;

	Response removeLike(RemoveLikeRequest request)
			throws NetworkRequestException;

	// PIN

	Response addPin(AddPinRequest request)
			throws NetworkRequestException;

	TopicsListResponse getPinFeed(GetPinFeedRequest request)
			throws NetworkRequestException;

	Response removePin(RemovePinRequest request)
			throws NetworkRequestException;

	Response hideTopic(HideTopicRequest request)
			throws NetworkRequestException;
}
