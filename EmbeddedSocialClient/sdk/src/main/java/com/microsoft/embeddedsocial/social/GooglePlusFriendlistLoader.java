/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.social;

import com.google.gson.Gson;
import com.microsoft.embeddedsocial.auth.SocialNetworkTokenHolder;
import com.microsoft.embeddedsocial.auth.SocialNetworkTokens;
import com.microsoft.embeddedsocial.social.exception.InvalidCredentialsException;
import com.microsoft.embeddedsocial.social.exception.NotAuthorizedToSocialNetworkException;
import com.microsoft.embeddedsocial.social.exception.SocialNetworkException;
import com.microsoft.embeddedsocial.autorest.models.IdentityProvider;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads Google Plus friends.
 */
class GooglePlusFriendlistLoader extends FriendlistLoader {

	private static final String GET_FRIENDS_URL = "https://www.googleapis.com/plus/v1/people/me/people/visible";
	private static final String HEADER_AUTHORIZATION = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";

	private static final int INVALID_CREDENTIALS_ERROR_CODE = 401;

	private final SocialNetworkTokenHolder<String> tokenHolder = SocialNetworkTokens.google();
	private final OkHttpClient httpClient = new OkHttpClient();

	GooglePlusFriendlistLoader() {
		super(IdentityProvider.GOOGLE);
	}

	@Override
	public List<String> getThirdPartyFriendIds() throws SocialNetworkException {
		if (!isAuthorizedToSocialNetwork()) {
			throw new NotAuthorizedToSocialNetworkException();
		}
		try {
			String token = tokenHolder.getToken();
			Response response = httpClient.newCall(
				new Request.Builder()
					.url(GET_FRIENDS_URL)
					.addHeader(HEADER_AUTHORIZATION, BEARER_PREFIX + token)
					.get()
					.build()
			).execute();
			GooglePeopleResponse result = new Gson().fromJson(response.body().string(), GooglePeopleResponse.class);
			result.throwExceptionIfNeeded();
			return extractPeopleIds(result);
		} catch (InvalidCredentialsException e) {
			throw e;
		} catch (IOException e) {
			throw new SocialNetworkException(e);
		} finally {
			tokenHolder.clearToken();
		}
	}

	@Override
	public boolean isAuthorizedToSocialNetwork() {
		return tokenHolder.hasToken();
	}

	private List<String> extractPeopleIds(GooglePeopleResponse response) {
		List<String> result = new ArrayList<>();

		if (response.items != null) {
			for (GooglePerson person : response.items) {
				result.add(person.id);
			}
		}

		return result;
	}

	@SuppressWarnings("unused")
	private class GooglePeopleResponse {
		private GooglePerson[] items;
		private ResponseError error;

		void throwExceptionIfNeeded() throws SocialNetworkException {
			if (error != null) {
				if (error.code == INVALID_CREDENTIALS_ERROR_CODE) {
					throw new InvalidCredentialsException(error.message);
				} else {
					throw new SocialNetworkException("code " + error.code + ": " + error.message);
				}
			}
		}
	}

	@SuppressWarnings("unused")
	private class GooglePerson {
		private String id;
	}

	@SuppressWarnings("unused")
	private class ResponseError {
		private int code;
		private String message;
	}
}
