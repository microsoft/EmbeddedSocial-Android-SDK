/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.account;

import com.microsoft.socialplus.autorest.models.IdentityProvider;
import com.microsoft.socialplus.autorest.models.PostUserRequest;
import com.microsoft.socialplus.autorest.models.PostUserResponse;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.socialplus.base.GlobalObjectRegistry;
import com.microsoft.socialplus.sdk.Options;
import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.microsoft.socialplus.server.model.UserRequest;
import com.microsoft.socialplus.server.model.auth.AuthenticationResponse;

import java.io.IOException;

public final class CreateUserRequest extends UserRequest {

	private PostUserRequest body;

	private CreateUserRequest() {
		body = new PostUserRequest();
	}

	@Override
	public AuthenticationResponse send() throws NetworkRequestException {
		ServiceResponse<PostUserResponse> serviceResponse;
		try {
			serviceResponse = USERS.postUser(body, appKey, bearerToken, null);
		} catch (ServiceException|IOException e) {
			throw new NetworkRequestException(e.getMessage());
		}
		checkResponseCode(serviceResponse);

		return new AuthenticationResponse(serviceResponse.getBody());
	}

	public static class Builder {

		private final CreateUserRequest request = new CreateUserRequest();

		public Builder setFirstName(String firstName) {
			request.body.setFirstName(firstName);
			return this;
		}

		public Builder setLastName(String lastName) {
			request.body.setLastName(lastName);
			return this;
		}

		public Builder setBio(String bio) {
			request.body.setBio(bio);
			return this;
		}

		public Builder setPhotoHandle(String photoHandle) {
			request.body.setPhotoHandle(photoHandle);
			return this;
		}

		public Builder setIdentityProvider(IdentityProvider identityProvider) {
			request.body.setIdentityProvider(identityProvider);
			return this;
		}

		public Builder setAccessToken(String accessToken) {
			request.body.setAccessToken(accessToken);
			return this;
		}

		public Builder setInstanceId(String instanceId) {
			request.body.setInstanceId(instanceId);
			return this;
		}

		public CreateUserRequest build() {
			if (request.body.getFirstName() == null
					|| request.body.getLastName() == null
					|| request.body.getIdentityProvider() == null
					|| request.body.getInstanceId() == null) {
				throw new IllegalArgumentException("one of the required fields was empty");
			}
			return request;
		}
	}
}
