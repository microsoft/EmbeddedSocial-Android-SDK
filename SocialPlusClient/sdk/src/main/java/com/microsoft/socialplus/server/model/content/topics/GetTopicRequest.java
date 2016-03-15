/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.content.topics;

import com.microsoft.autorest.models.TopicView;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;

import java.io.IOException;

/**
 * This class should be specific to GetTopic
 * Currently, other requests, such as delete topic
 * also use this Request class
 */
public class GetTopicRequest extends GenericTopicRequest {

	public GetTopicRequest(String topicHandle) {
		super(topicHandle);
	}

	@Override
	public GetTopicResponse send() throws ServiceException, IOException {
		ServiceResponse<TopicView> serviceResponse =
				TOPICS.getTopic(topicHandle, appKey, bearerToken);
		return new GetTopicResponse(
				new com.microsoft.socialplus.server.model.view.TopicView(serviceResponse.getBody()));
	}
}
