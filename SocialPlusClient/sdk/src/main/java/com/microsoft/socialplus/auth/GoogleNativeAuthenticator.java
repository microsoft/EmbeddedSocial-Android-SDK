/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.auth;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;

import com.microsoft.socialplus.autorest.models.IdentityProvider;
import com.microsoft.socialplus.base.GlobalObjectRegistry;
import com.microsoft.socialplus.base.utils.debug.DebugLog;
import com.microsoft.socialplus.sdk.Options;
import com.microsoft.socialplus.sdk.R;
import com.microsoft.socialplus.ui.activity.GoogleCallbackActivity;
import com.microsoft.socialplus.ui.util.SocialNetworkAccount;

import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ResponseTypeValues;

/**
 * Implements Google authentication process using Google Play Services SDK.
 */
public class GoogleNativeAuthenticator extends AbstractAuthenticator {
	public static final String GOOGLE_ACCOUNT_ACTION = "googleAccountAction";
	public static final String GOOGLE_ACCOUNT = "googleAccount";

	private static final Uri ISSUER_URI = Uri.parse("https://accounts.google.com");
	private AuthorizationService service;
	private Context context;

	public GoogleNativeAuthenticator(Fragment fragment, IAuthenticationCallback authCallback) {
		super(IdentityProvider.GOOGLE, fragment, authCallback);

		context = getFragment().getContext();
		service = new AuthorizationService(context);

		LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getFragment().getContext());
		localBroadcastManager.registerReceiver(googleAuthReciever, new IntentFilter(GOOGLE_ACCOUNT_ACTION));
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
							LocalBroadcastManager.getInstance(context).unregisterReceiver(googleAuthReciever);
							onAuthenticationError(getFragment().getString(R.string.sp_msg_google_signin_failed));
						} else {
							// service configuration retrieved, proceed to authorization...'
							sendAuthRequest(serviceConfiguration);
						}
				});
	}

	private void sendAuthRequest(AuthorizationServiceConfiguration serviceConfiguration) {
		Options options = GlobalObjectRegistry.getObject(Options.class);
		String clientId = options.getGoogleClientId();
		Uri redirectUri = Uri.parse(context.getString(R.string.sp_google_auth_redirect));

		AuthorizationRequest request = new AuthorizationRequest.Builder(
				serviceConfiguration,
				clientId,
				ResponseTypeValues.CODE,
				redirectUri)
				.setScope("profile") // TODO "email" needed for find friends
				.build();

		PendingIntent pendingIntent = GoogleCallbackActivity.createPostAuthorizationIntent(context, request);
		service.performAuthorizationRequest(request, pendingIntent);
		service.dispose();
	}

	private BroadcastReceiver googleAuthReciever = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			SocialNetworkAccount account = intent.getParcelableExtra(GOOGLE_ACCOUNT);
			if (account != null) {
				onAuthenticationSuccess(account);
			} else {
				onAuthenticationError(getFragment().getString(R.string.sp_msg_google_signin_failed));
			}

			LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
		}
	};
}
