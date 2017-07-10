/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.auth;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;

import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.autorest.models.IdentityProvider;
import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.sdk.Options;
import com.microsoft.embeddedsocial.ui.activity.GoogleCallbackActivity;
import com.microsoft.embeddedsocial.ui.util.SocialNetworkAccount;

import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ResponseTypeValues;

import java.util.Arrays;
import java.util.List;

/**
 * Implements Google authentication process using Google Play Services SDK.
 */
public class GoogleNativeAuthenticator extends AbstractAuthenticator {
	public static final String GOOGLE_ACCOUNT_ACTION = "googleAccountAction";
	public static final String GOOGLE_ACCOUNT = "googleAccount";
	private final AuthenticationMode authMode;

	private static final Uri ISSUER_URI = Uri.parse("https://accounts.google.com");
	private AuthorizationService service;
	private Context context;

	public GoogleNativeAuthenticator(Fragment fragment, IAuthenticationCallback authCallback,
									 AuthenticationMode authMode) {
		super(IdentityProvider.GOOGLE, fragment, authCallback);

		context = getFragment().getContext();
		service = new AuthorizationService(context);

		LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getFragment().getContext());
		localBroadcastManager.registerReceiver(googleAuthReceiver, new IntentFilter(GOOGLE_ACCOUNT_ACTION));
		this.authMode = authMode;
	}

	@Override
	protected void onAuthenticationStarted() throws AuthenticationException {
		makeAuthRequest();
	}

	public void makeAuthRequest() {
		AuthorizationServiceConfiguration.fetchFromIssuer(
				ISSUER_URI,
				(@Nullable AuthorizationServiceConfiguration serviceConfiguration,
							@Nullable AuthorizationException ex) -> {
						if (ex != null) {
							DebugLog.logException(ex);
							service.dispose();
							LocalBroadcastManager.getInstance(context).unregisterReceiver(googleAuthReceiver);
							onAuthenticationError(getFragment().getString(R.string.es_msg_google_signin_failed));
						} else {
							// service configuration retrieved, proceed to authorization...'
							sendAuthRequest(serviceConfiguration);
						}
				});
	}

	private void sendAuthRequest(AuthorizationServiceConfiguration serviceConfiguration) {
		Options options = GlobalObjectRegistry.getObject(Options.class);
		String clientId = options.getGoogleClientId();
		String authRedirect = String.format("%s:/oauth2redirect", context.getPackageName());

		Uri redirectUri = Uri.parse(authRedirect);

		AuthorizationRequest request = new AuthorizationRequest.Builder(
				serviceConfiguration,
				clientId,
				ResponseTypeValues.CODE,
				redirectUri)
				.setScopes(authMode.getPermissions())
				.build();

		PendingIntent pendingIntent = GoogleCallbackActivity.createPostAuthorizationIntent(context, request);
		service.performAuthorizationRequest(request, pendingIntent);
		service.dispose();
	}

	private BroadcastReceiver googleAuthReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			SocialNetworkAccount account = intent.getParcelableExtra(GOOGLE_ACCOUNT);
			intent.removeExtra(GOOGLE_ACCOUNT);
			if (account != null) {
				if (authMode.canStoreToken()) {
					SocialNetworkTokens.google().storeToken(account.getThirdPartyAccessToken());
				}
				onAuthenticationSuccess(account);
			} else {
				onAuthenticationError(getFragment().getString(R.string.es_msg_google_signin_failed));
			}

			LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
		}
	};

	/**
	 * Google authentication mode.
	 */
	public enum AuthenticationMode {

		/**
		 * Allow sign-in only.
		 */
		SIGN_IN_ONLY(false, "profile"),

		/**
		 * Allow sign-in and obtaining friend list.
		 */
		OBTAIN_FRIENDS(true, "profile", "email");

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
