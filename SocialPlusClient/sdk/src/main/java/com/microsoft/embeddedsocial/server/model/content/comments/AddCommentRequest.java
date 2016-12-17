/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model.content.comments;

import com.microsoft.embeddedsocial.autorest.models.BlobType;
import com.microsoft.embeddedsocial.autorest.models.PostCommentRequest;
import com.microsoft.embeddedsocial.autorest.models.PostCommentResponse;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.UserRequest;

import java.io.IOException;

public class AddCommentRequest extends UserRequest {

	private final String topicHandle;
	private final PostCommentRequest request;

	public AddCommentRequest(String topicHandle, String contentText, BlobType contentBlobType, String contentBlobUrl) {
		this.topicHandle = topicHandle;
		request = new PostCommentRequest();
		request.setText(contentText);
		request.setBlobType(contentBlobType);
		request.setBlobHandle(contentBlobUrl);
		request.setLanguage(language);
	}

	public AddCommentRequest(String topicHandle, String contentText) {
		this(topicHandle, contentText, BlobType.UNKNOWN, null);
	}

	@Override
	public AddCommentResponse send() throws NetworkRequestException {
		ServiceResponse<PostCommentResponse> serviceResponse;
		try {
			serviceResponse = TOPIC_COMMENTS.postComment(topicHandle, request, authorization);
		} catch (ServiceException|IOException e) {
			throw new NetworkRequestException(e.getMessage());
		}
		checkResponseCode(serviceResponse);

		return new AddCommentResponse(serviceResponse.getBody().getCommentHandle());
	}
}
