/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model.content.topics;

import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.BaseRequest;
import com.microsoft.embeddedsocial.autorest.models.PutTopicRequest;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;

import java.io.IOException;

import retrofit2.Response;

public class UpdateTopicRequest extends GenericTopicRequest {

	private final PutTopicRequest request;

	public UpdateTopicRequest(String topicHandle, String topicTitle, String topicText, String topicCategories) {
		super(topicHandle);
		request = new PutTopicRequest();
		request.setTitle(topicTitle);
		request.setText(topicText);
		request.setCategories(topicCategories);
	}

	@Override
	public Response send() throws NetworkRequestException {
		ServiceResponse<Object> serviceResponse;
		try {
			serviceResponse = BaseRequest.TOPICS.putTopic(topicHandle, request, authorization);
		} catch (ServiceException|IOException e) {
			throw new NetworkRequestException(e.getMessage());
		}
		checkResponseCode(serviceResponse);

		return serviceResponse.getResponse();
	}
}
