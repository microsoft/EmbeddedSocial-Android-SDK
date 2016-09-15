/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.auth;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

import com.microsoft.socialplus.autorest.models.IdentityProvider;
import com.microsoft.socialplus.ui.activity.SignInActivity;

import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ResponseTypeValues;

/**
 * Implements Google authentication process using Google Play Services SDK.
 */
public class GoogleNativeAuthenticator extends AbstractAuthenticator {
	private AuthorizationService service;
	private Context context;

	public GoogleNativeAuthenticator(Fragment fragment, IAuthenticationCallback authCallback) {
		super(IdentityProvider.GOOGLE, fragment, authCallback);

		context = getFragment().getContext();
		service = new AuthorizationService(context);
	}

	public void makeAuthRequest() {
		AuthorizationServiceConfiguration serviceConfiguration = new AuthorizationServiceConfiguration(
				Uri.parse("https://accounts.google.com/o/oauth2/v2/auth") /* auth endpoint */,
				Uri.parse("https://www.googleapis.com/oauth2/v4/token") /* token endpoint */,
				null
		);

		String clientId = "780162482042-2rpd1e6fq2517u3i7moo2lfppan675v5.apps.googleusercontent.com";
		Uri redirectUri = Uri.parse("com.microsoft.socialplus:/oauth2redirect");

		AuthorizationRequest request = new AuthorizationRequest.Builder(
				serviceConfiguration,
				clientId,
				ResponseTypeValues.CODE,
				redirectUri)
				.setScope("profile email") //TODO set scope appropriately
				.build();

		PendingIntent pendingIntent = SignInActivity.createPostAuthorizationIntent(context, request);
		service.performAuthorizationRequest(request, pendingIntent);
	}

	@Override
	protected void onAuthenticationStarted() throws AuthenticationException {
		makeAuthRequest();
	}
}
