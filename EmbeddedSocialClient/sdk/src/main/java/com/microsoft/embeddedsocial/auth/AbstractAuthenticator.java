/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.auth;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.microsoft.embeddedsocial.autorest.models.IdentityProvider;
import com.microsoft.embeddedsocial.base.IDisposable;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.ui.util.SocialNetworkAccount;

/**
 * Base class for all authenticators allowing to use 3rd party accounts to log in to Embedded Social.
 */
public abstract class AbstractAuthenticator implements IDisposable {

	private final IdentityProvider identityProvider;
	private final Fragment fragment;
	private final IAuthenticationCallback authCallback;

	protected AbstractAuthenticator(IdentityProvider accountType, Fragment fragment, IAuthenticationCallback authCallback) {
		this.identityProvider = accountType;
		this.fragment = fragment;
		this.authCallback = authCallback;
	}

	protected Fragment getFragment() {
		return fragment;
	}

	public IdentityProvider getAccountType() {
		return identityProvider;
	}

	/**
	 * Should be called when an error occurs during authentication process.
	 *
	 * @param errorMessage error message shown to the user
	 */
	protected void onAuthenticationError(String errorMessage) {
		authCallback.onAuthenticationError(errorMessage);
	}

	/**
	 * Should be called when an error occurs during authentication process.
	 * Uses {@link #onAuthenticationError(String)} with default error message.
	 */
	protected void onGeneralAuthenticationError() {
		authCallback.onAuthenticationError(getFragment().getString(R.string.es_msg_general_signin_error));
	}

	/**
	 * Should be called when authentication completes successfully.
	 *
	 * @param account authenticated user account
	 */
	protected void onAuthenticationSuccess(SocialNetworkAccount account) {
		authCallback.onAuthenticationSuccess(account);
	}

	/**
	 * Starts authentication process asynchronously. Its results are delivered via
	 * {@linkplain IAuthenticationCallback}. Calling this method guarantees that callback
	 * is going to be called.
	 */
	public final void startAuthenticationAsync() {
		try {
			onAuthenticationStarted();
		} catch (AuthenticationException e) {
			DebugLog.logException(e);
			// FIXME move message to resources
			onAuthenticationError("error during authentication");
		}
	}

	/**
	 * Does nothing in default implementation.
	 */
	@Override
	public void dispose() {
	}

	/**
	 * Processes {@link Fragment#onActivityResult(int, int, Intent)} calls.
	 * <br/> Default implementation does nothing.
	 *
	 * @param requestCode request code
	 * @param resultCode  result code
	 * @param data        result data
	 * @return true if request was processed, false otherwise.
	 */
	public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
		return false;
	}

	/**
	 * Is called when authentication process starts.
	 *
	 * @throws AuthenticationException if authentication process can't be started.
	 */
	protected abstract void onAuthenticationStarted() throws AuthenticationException;
}
