/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.auth;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.microsoft.embeddedsocial.base.utils.ConnectionUtils;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.ui.util.SocialNetworkAccount;
import com.microsoft.embeddedsocial.autorest.models.IdentityProvider;

import java.util.Arrays;
import java.util.List;

/**
 * Implements Facebook authentication process.
 */
public class FacebookAuthenticator extends AbstractAuthenticator {

	private final CallbackManager facebookCallbackManager = CallbackManager.Factory.create();
	private final FacebookProfileTracker facebookProfileTracker = new FacebookProfileTracker();
	private final GeneralFacebookCallback generalFacebookCallback = new GeneralFacebookCallback();
	private final AuthenticationMode authMode;

	public FacebookAuthenticator(Fragment fragment, IAuthenticationCallback authCallback,
								 AuthenticationMode authMode) {
		super(IdentityProvider.FACEBOOK, fragment, authCallback);
		this.authMode = authMode;
	}

	@Override
	protected void onAuthenticationStarted() throws AuthenticationException {
		LoginManager.getInstance().registerCallback(facebookCallbackManager, generalFacebookCallback);
		LoginManager.getInstance().setLoginBehavior(LoginBehavior.SUPPRESS_SSO);
		LoginManager.getInstance().logInWithReadPermissions(
			getFragment(),
			authMode.getPermissions()
		);
	}

	private void notifySignInSuccess(Profile profile) {
		SocialNetworkAccount socialNetworkAccount = new SocialNetworkAccount(
				IdentityProvider.FACEBOOK,
				profile.getId(),
				AccessToken.getCurrentAccessToken().getToken(),
				profile.getFirstName(),
				profile.getLastName()
		);
		if (authMode.canStoreToken()) {
			SocialNetworkTokens.facebook().storeToken(AccessToken.getCurrentAccessToken());
		}
		onAuthenticationSuccess(socialNetworkAccount);
	}

	@Override
	public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
		facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
		return true;
	}

	@Override
	public void dispose() {
		if (facebookProfileTracker.isTracking()) {
			facebookProfileTracker.stopTracking();
		}
		super.dispose();
	}

	private class GeneralFacebookCallback implements FacebookCallback<LoginResult> {
		@Override
		public void onSuccess(LoginResult loginResult) {
			DebugLog.i("Facebook login success");
			if (Profile.getCurrentProfile() == null) {
				DebugLog.i("Can't get access to FB profile");
				facebookProfileTracker.startTracking();
			} else {
				notifySignInSuccess(Profile.getCurrentProfile());
			}
		}

		@Override
		public void onCancel() {
			int signInErrorId = R.string.es_message_facebook_login_cancel;
			if (!ConnectionUtils.isConnectingToInternet(getFragment().getActivity().getBaseContext())) {
				signInErrorId = R.string.es_message_no_internet_connection;
			}
			FacebookAuthenticator.this.onAuthenticationError(getFragment().getString(signInErrorId));
		}

		@Override
		public void onError(FacebookException exception) {
			FacebookAuthenticator.this.onAuthenticationError(
				getFragment().getString(R.string.es_message_facebook_error_pattern, exception.getMessage()));
		}
	}

	private class FacebookProfileTracker extends ProfileTracker {
		@Override
		protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
			if (profile2 != null) {
				notifySignInSuccess(profile2);
				if (facebookProfileTracker.isTracking()) {
					facebookProfileTracker.stopTracking();
				}
			}
		}
	}

	/**
	 * Facebook authentication mode.
	 */
	public enum AuthenticationMode {

		/**
		 * Allow sign-in only.
		 */
		SIGN_IN_ONLY(false, "public_profile"),

		/**
		 * Allow sign-in and obtaining friend list.
		 */
		OBTAIN_FRIENDS(true, "public_profile", "user_friends");

		private final List<String> permissions;
		private final boolean allowStoringToken;

		AuthenticationMode(boolean allowStoringToken, String... permissions) {
			this.permissions = Arrays.asList(permissions);
			this.allowStoringToken = allowStoringToken;
		}

		private List<String> getPermissions() {
			return permissions;
		}

		private boolean canStoreToken() {
			return allowStoringToken;
		}
	}
}
