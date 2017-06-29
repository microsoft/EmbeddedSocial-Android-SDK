/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.social;

import android.os.Bundle;
import android.text.TextUtils;

import com.facebook.AccessToken;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.microsoft.embeddedsocial.auth.SocialNetworkTokenHolder;
import com.microsoft.embeddedsocial.auth.SocialNetworkTokens;
import com.microsoft.embeddedsocial.autorest.models.IdentityProvider;
import com.microsoft.embeddedsocial.social.exception.NotAuthorizedToSocialNetworkException;
import com.microsoft.embeddedsocial.social.exception.SocialNetworkException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads Facebook friends.
 */
class FacebookFriendlistLoader extends FriendlistLoader {

	private static final String FACEBOOK_FRIENDS_GRAPH_PATH = "/me/friends";

	private final SocialNetworkTokenHolder<AccessToken> facebookTokenHolder = SocialNetworkTokens.facebook();

	FacebookFriendlistLoader() {
		super(IdentityProvider.FACEBOOK);
	}

	private static List<String> extractFacebookFriendIds(GraphResponse graphResponse)
		throws SocialNetworkException {

		List<String> result = new ArrayList<>();
		Gson gson = new Gson();
		FacebookFriendsResponse response = gson.fromJson(graphResponse.getRawResponse(),
			FacebookFriendsResponse.class);
		OkHttpClient httpClient = new OkHttpClient();

		while (response != null) {
			for (FacebookUser user : response.data) {
				result.add(user.id);
			}
			try {
				if (response.paging != null && response.paging.hasNext()) {
					Response webResponse = httpClient.newCall(
						new Request.Builder()
							.url(response.paging.nextPageUrl)
							.get()
							.build()
					).execute();
					response = gson.fromJson(webResponse.body().string(), FacebookFriendsResponse.class);
				} else {
					response = null;
				}
			} catch (IOException e) {
				throw new SocialNetworkException(e);
			}
		}

		return result;
	}

	@Override
	public List<String> getThirdPartyFriendIds() throws SocialNetworkException {
		if (!isAuthorizedToSocialNetwork()) {
			throw new NotAuthorizedToSocialNetworkException();
		}
		GraphResponse response = new GraphRequest(
			facebookTokenHolder.getToken(),
			FACEBOOK_FRIENDS_GRAPH_PATH,
			Bundle.EMPTY,
			HttpMethod.GET
		).executeAndWait();

		facebookTokenHolder.clearToken();

		FacebookRequestError responseError = response.getError();
		if (responseError != null) {
			throw new SocialNetworkException("Internal facebook failure: "
				+ responseError.getErrorMessage() + " [" + responseError.getErrorCode() + "]");
		}

		return extractFacebookFriendIds(response);
	}

	@Override
	public boolean isAuthorizedToSocialNetwork() {
		return facebookTokenHolder.hasToken();
	}

	@SuppressWarnings("unused")
	private class FacebookFriendsResponse {
		private FacebookUser[] data;
		private FacebookPaging paging;
	}

	@SuppressWarnings("unused")
	private class FacebookUser {
		private String id;
		private String name;
	}

	@SuppressWarnings("unused")
	private class FacebookPaging {

		@SerializedName("next")
		private String nextPageUrl;

		private boolean hasNext() {
			return !TextUtils.isEmpty(nextPageUrl);
		}
	}
}
