/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model.content.comments;

import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.FeedUserRequest;
import com.microsoft.embeddedsocial.autorest.models.FeedResponseCommentView;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.embeddedsocial.data.model.CommentFeedType;

import java.io.IOException;

public class GetCommentFeedRequest extends FeedUserRequest {

	protected String topicHandle;
	private final int commentFeedType;

	public GetCommentFeedRequest(CommentFeedType commentFeedType, String topicHandle) {
		this.commentFeedType = commentFeedType.ordinal();
		this.topicHandle = topicHandle;
	}

	public int getCommentFeedType() {
		return commentFeedType;
	}

	public String getTopicHandle() {
		return topicHandle;
	}

	public void setTopicHandle(String topicHandle) {
		this.topicHandle = topicHandle;
	}

	@Override
	public GetCommentFeedResponse send() throws NetworkRequestException {
		ServiceResponse<FeedResponseCommentView> serviceResponse;
		try {
			serviceResponse = TOPIC_COMMENTS.getTopicComments(topicHandle, authorization,
					getCursor(), getBatchSize());
		} catch (ServiceException|IOException e) {
			throw new NetworkRequestException(e.getMessage());
		}
		checkResponseCode(serviceResponse);
		return new GetCommentFeedResponse(serviceResponse.getBody());
	}
}
