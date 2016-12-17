/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.social;

import com.microsoft.embeddedsocial.autorest.models.IdentityProvider;

/**
 * Provides access to social network friend loaders.
 */
public final class FriendlistLoaders {

	private FriendlistLoaders() {  }

	public static FriendlistLoader newFriendlistLoader(IdentityProvider accountType) {
		switch (accountType) {
			case FACEBOOK:
				return new FacebookFriendlistLoader();

			case GOOGLE:
				return new GooglePlusFriendlistLoader();

			case MICROSOFT:
				return new MicrosoftLiveFriendlistLoader();

			default:
				throw new RuntimeException("account type is not supported");
		}
	}
}
