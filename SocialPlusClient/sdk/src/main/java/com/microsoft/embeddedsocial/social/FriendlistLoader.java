/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.social;

import com.microsoft.embeddedsocial.social.exception.SocialNetworkException;
import com.microsoft.embeddedsocial.autorest.models.IdentityProvider;

import java.util.List;

/**
 * Base friend loader class responsible for loading friend lists from social networks.
 */
public abstract class FriendlistLoader {

	private final IdentityProvider identityProvider;

	protected FriendlistLoader(IdentityProvider identityProvider) {
		this.identityProvider = identityProvider;
	}

	/**
	 * Gets the list of friend ids from a social network supported by this friend loader.
	 *
	 * @return list of friend ids.
	 * @throws SocialNetworkException if anything goes wrong.
	 */
	public abstract List<String> getThirdPartyFriendIds() throws SocialNetworkException;

	/**
	 * Gets social network account type supported by this friend loader.
	 *
	 * @return one of {@link IdentityProvider} values.
	 */
	public final IdentityProvider getAccountType() {
		return identityProvider;
	}

	public abstract boolean isAuthorizedToSocialNetwork();

}
