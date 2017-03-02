/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.auth;

import com.facebook.AccessToken;
import com.microsoft.live.LiveConnectSession;

/**
 * Provides access to social network access tokens.
 */
public class SocialNetworkTokens {

	private static final SocialNetworkTokenHolder<AccessToken> FACEBOOK_TOKEN = new SocialNetworkTokenHolder<>();
	private static final SocialNetworkTokenHolder<LiveConnectSession> MICROSOFT_TOKEN = new SocialNetworkTokenHolder<>();
	private static final SocialNetworkTokenHolder<String> GOOGLE_TOKEN = new SocialNetworkTokenHolder<>();

	/**
	 * Gets token holder for Facebook access token.
	 * @return  {@link SocialNetworkTokenHolder} instance for Facebook.
	 */
	public static SocialNetworkTokenHolder<AccessToken> facebook() {
		return FACEBOOK_TOKEN;
	}

	/**
	 * Gets token holder for Microsoft access token.
	 * @return  {@link SocialNetworkTokenHolder} instance for Microsoft.
	 */
	public static SocialNetworkTokenHolder<LiveConnectSession> microsoft() {
		return MICROSOFT_TOKEN;
	}

	/**
	 * Gets token holder for Google access token.
	 * @return  {@link SocialNetworkTokenHolder} instance for Google.
	 */
	public static SocialNetworkTokenHolder<String> google() {
		return GOOGLE_TOKEN;
	}

	/**
	 * Clears all stored access tokens.
	 */
	public static void clearAll() {
		SocialNetworkTokenHolder[] tokenHolders = {GOOGLE_TOKEN, FACEBOOK_TOKEN, MICROSOFT_TOKEN};

		for (SocialNetworkTokenHolder<?> token : tokenHolders) {
			token.clearToken();
		}
	}
}
