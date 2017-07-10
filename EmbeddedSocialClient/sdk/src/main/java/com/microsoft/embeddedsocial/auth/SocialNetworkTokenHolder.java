/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.auth;

/**
 * Holds access tokens for social networks.
 * @param <T>   access token type
 */
public class SocialNetworkTokenHolder<T> {

	private T token;

	SocialNetworkTokenHolder() {  }

	/**
	 * Checks if this holder has a valid access token.
	 * @return true if a valid access token is present
	 */
	public boolean hasToken() {
		return token != null;
	}

	/**
	 * Gets current access token.
	 * @return  access token instance.
	 * @see     {@link #hasToken()}.
	 */
	public T getToken() {
		return token;
	}

	/**
	 * Stores an access token.
	 * @param token access token
	 */
	public void storeToken(T token) {
		this.token = token;
	}

	/**
	 * Clears currently stored token.
	 */
	public void clearToken() {
		storeToken(null);
	}
}
