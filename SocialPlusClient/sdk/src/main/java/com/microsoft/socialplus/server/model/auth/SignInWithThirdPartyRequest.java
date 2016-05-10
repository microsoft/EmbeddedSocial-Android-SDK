/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.auth;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.microsoft.socialplus.autorest.models.IdentityProvider;
import com.microsoft.socialplus.autorest.models.PostSessionRequest;
import com.microsoft.socialplus.autorest.models.PostSessionResponse;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.socialplus.sdk.R;
import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.microsoft.socialplus.server.model.UserRequest;
import com.microsoft.socialplus.ui.activity.FollowersActivity;
import com.microsoft.socialplus.ui.activity.SignInActivity;

import java.io.IOException;

/**
 *
 */
public class SignInWithThirdPartyRequest extends UserRequest {

	private final PostSessionRequest request;

	public SignInWithThirdPartyRequest(
			IdentityProvider identityProvider,
			String thirdPartyAccessToken) {
		request = new PostSessionRequest();
		request.setIdentityProvider(identityProvider);
		request.setAccessToken(thirdPartyAccessToken);
		//TODO
		//request.setRequestToken(requestToken);
		request.setInstanceId(instanceId);
		request.setCreateUser(false);
	}

	@Override
	public AuthenticationResponse send() throws NetworkRequestException {
		ServiceResponse<PostSessionResponse> serviceResponse;
		try {
			serviceResponse = SESSION.postSession(request, appKey, bearerToken, null);
		} catch (ServiceException|IOException e) {
			throw new NetworkRequestException(e.getMessage());
		}

		if (!request.getCreateUser() && serviceResponse.getResponse().code() == 404) {
			request.setCreateUser(true);
			return send();
		}

		checkResponseCode(serviceResponse);

		int messageId = 0; // invalid message id
		if (serviceResponse.getResponse().isSuccess()) {
			if (request.getCreateUser()) {
				messageId = R.string.sp_msg_general_create_user_success;
			} else {
				messageId = R.string.sp_msg_general_signin_success;
			}
		}

		return new AuthenticationResponse(serviceResponse.getBody(), messageId);
	}
}
