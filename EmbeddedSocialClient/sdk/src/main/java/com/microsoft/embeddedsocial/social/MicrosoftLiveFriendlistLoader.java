/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.social;

import com.google.gson.Gson;
import com.microsoft.embeddedsocial.autorest.models.IdentityProvider;
import com.microsoft.live.LiveConnectClient;
import com.microsoft.live.LiveConnectSession;
import com.microsoft.live.LiveOperation;
import com.microsoft.live.LiveOperationException;
import com.microsoft.embeddedsocial.auth.SocialNetworkTokenHolder;
import com.microsoft.embeddedsocial.auth.SocialNetworkTokens;
import com.microsoft.embeddedsocial.social.exception.NotAuthorizedToSocialNetworkException;
import com.microsoft.embeddedsocial.social.exception.SocialNetworkException;

import java.util.ArrayList;
import java.util.List;

/**
 * Loads Microsoft Live friends.
 */
class MicrosoftLiveFriendlistLoader extends FriendlistLoader {

	private static final String CONTACTS_PATH = "me/contacts";
	private final SocialNetworkTokenHolder<LiveConnectSession> tokenHolder = SocialNetworkTokens.microsoft();

	MicrosoftLiveFriendlistLoader() {
		super(IdentityProvider.MICROSOFT);
	}

	@Override
	public List<String> getThirdPartyFriendIds() throws SocialNetworkException {
		if (!isAuthorizedToSocialNetwork()) {
			throw new NotAuthorizedToSocialNetworkException();
		}

		LiveConnectClient liveConnectClient = new LiveConnectClient(tokenHolder.getToken());
		try {
			LiveOperation getFriendsOperation = liveConnectClient.get(CONTACTS_PATH);
			return extractFriendIds(getFriendsOperation.getRawResult());
		} catch (LiveOperationException e) {
			throw new SocialNetworkException(e);
		} finally {
			tokenHolder.clearToken();
		}
	}

	@Override
	public boolean isAuthorizedToSocialNetwork() {
		return tokenHolder.hasToken();
	}

	private List<String> extractFriendIds(String rawResult) {
		FriendsResponse response = new Gson().fromJson(rawResult, FriendsResponse.class);
		List<String> result = new ArrayList<>();

		for (PersonId personId : response.data) {
			result.add(personId.id);
		}

		return result;
	}

	@SuppressWarnings("unused")
	private class FriendsResponse {
		private PersonId[] data;
	}

	@SuppressWarnings("unused")
	private class PersonId {
		private String id;
	}
}
