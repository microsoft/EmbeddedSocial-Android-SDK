/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model.content.topics;

import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.BaseRequest;
import com.microsoft.embeddedsocial.autorest.models.TopicView;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;

import java.io.IOException;

/**
 * This class should be specific to GetTopic
 * Currently, other requests, such as delete topic
 * also use this Request class
 */
public class GetTopicRequest extends GenericTopicRequest {

	private String existingImagePath;

	public GetTopicRequest(String topicHandle) {
		this(topicHandle, null);
	}

	public GetTopicRequest(String topicHandle, String existingImagePath) {
		super(topicHandle);
		this.existingImagePath = existingImagePath;
	}

	@Override
	public GetTopicResponse send() throws NetworkRequestException {
		ServiceResponse<TopicView> serviceResponse;
		try {
			serviceResponse = BaseRequest.TOPICS.getTopic(topicHandle, authorization);
		} catch (ServiceException|IOException e) {
			throw new NetworkRequestException(e.getMessage());
		}
		checkResponseCode(serviceResponse);

		com.microsoft.embeddedsocial.server.model.view.TopicView topic =
				new com.microsoft.embeddedsocial.server.model.view.TopicView(serviceResponse.getBody());
		if (existingImagePath != null) {
			topic.setLocalImage(existingImagePath);
		}
		return new GetTopicResponse(topic);
	}
}
