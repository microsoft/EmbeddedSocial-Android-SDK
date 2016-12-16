/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.auth;

import android.support.annotation.UiThread;

import com.microsoft.embeddedsocial.ui.util.SocialNetworkAccount;

/**
 * Callback interface for authentication process.
 */
public interface IAuthenticationCallback {

	/**
	 * Is called when 3rd party authentication succeeds.
	 * @param account   user account data
	 */
	@UiThread
	void onAuthenticationSuccess(SocialNetworkAccount account);

	/**
	 * Is called when 3rd party authentication fails.
	 * @param errorMessage  failure reason
	 */
	@UiThread
	void onAuthenticationError(String errorMessage);
}
