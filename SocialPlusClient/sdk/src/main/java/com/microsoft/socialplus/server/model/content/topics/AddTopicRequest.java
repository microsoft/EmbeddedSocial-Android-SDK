/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.content.topics;

import com.microsoft.autorest.models.BlobType;
import com.microsoft.autorest.models.PostTopicRequest;
import com.microsoft.autorest.models.PostTopicResponse;
import com.microsoft.autorest.models.PublisherType;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.socialplus.server.model.UserRequest;

import java.io.IOException;

public final class AddTopicRequest extends UserRequest {

	private PostTopicRequest body;

	private AddTopicRequest() {
		body = new PostTopicRequest();
	}

	@Override
	public AddTopicResponse send() throws ServiceException, IOException {
		ServiceResponse<PostTopicResponse> serviceResponse =
				TOPICS.postTopic(body, bearerToken);
		return new AddTopicResponse(serviceResponse.getBody().getTopicHandle());
	}

	public static class Builder {

		private final AddTopicRequest request = new AddTopicRequest();

		public Builder setPublisherType(PublisherType publisherType) {
			request.body.setPublisherType(publisherType);
			return this;
		}

		public Builder setTopicTitle(String topicTitle) {
			request.body.setTitle(topicTitle);
			return this;
		}

		public Builder setTopicText(String topicText) {
			request.body.setText(topicText);
			return this;
		}

		public Builder setTopicBlobType(BlobType blobType) {
			request.body.setBlobType(blobType);
			return this;
		}

		public Builder setTopicBlobHandle(String blobHandle) {
			request.body.setBlobHandle(blobHandle);
			return this;
		}

		public Builder setTopicCategories(String categories) {
			request.body.setCategories(categories);
			return this;
		}

		public Builder setTopicLanguage(String language) {
			request.body.setLanguage(language);
			return this;
		}

		public Builder setTopicDeepLink(String topicDeepLink) {
			request.body.setDeepLink(topicDeepLink);
			return this;
		}

		public Builder setTopicFriendlyName(String friendlyName) {
			request.body.setFriendlyName(friendlyName);
			return this;
		}

		public Builder setTopicGroup(String group) {
			request.body.setGroup(group);
			return this;
		}

		public AddTopicRequest build() {
			if (request.body.getText() == null || request.body.getPublisherType() == null) {
				throw new IllegalStateException("some required fields were empty!");
			}
			return request;
		}
	}
}
