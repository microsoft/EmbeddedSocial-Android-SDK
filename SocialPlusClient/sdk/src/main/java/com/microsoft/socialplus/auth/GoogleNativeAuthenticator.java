/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.auth;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.microsoft.autorest.models.IdentityProvider;
import com.microsoft.socialplus.base.utils.debug.DebugLog;
import com.microsoft.socialplus.base.utils.thread.ThreadUtils;
import com.microsoft.socialplus.sdk.R;
import com.microsoft.socialplus.ui.util.SocialNetworkAccount;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Implements Google authentication process using Google Play Services SDK.
 */
public class GoogleNativeAuthenticator extends AbstractAuthenticator {

	private static final int RC_GOOGLE_SIGNIN_RESOLUTION = 1000;
	private static final int RC_GOOGLE_AUTH_TOKEN_RESOLUTION = 1001;

	private final ExecutorService executor = Executors.newSingleThreadExecutor();

	private GoogleApiClient googleApiClient;
	private volatile boolean googleApiClientConnected = false;

	public GoogleNativeAuthenticator(Fragment fragment, IAuthenticationCallback authCallback) {
		super(IdentityProvider.GOOGLE, fragment, authCallback);
		this.googleApiClient = new GoogleApiClient.Builder(fragment.getActivity())
			.addConnectionCallbacks(new GoogleSigninConnectionCallbacks())
			.addOnConnectionFailedListener(new GoogleSigninFailListener())
			.addApi(Plus.API)
			.addScope(new Scope(Scopes.PLUS_LOGIN))
			.build();
	}

	private void obtainGoogleAuthToken() {
		String accountName = Plus.AccountApi.getAccountName(googleApiClient);
		try {
			String token = GoogleAuthUtil.getToken(getFragment().getActivity(), accountName, "oauth2: "
				+ TextUtils.join(" ", new String[]{Scopes.PROFILE, Scopes.EMAIL}));
			SocialNetworkAccount account = new SocialNetworkAccount(
					IdentityProvider.GOOGLE,
				GoogleAuthUtil.getAccountId(getFragment().getContext(), accountName),
				token
			);
			SocialNetworkTokens.google().storeToken(token);
			ThreadUtils.runOnMainThread(() -> onAuthenticationSuccess(account));
		} catch (UserRecoverableAuthException e) {
			DebugLog.logException(e);
			getFragment().startActivityForResult(e.getIntent(), RC_GOOGLE_AUTH_TOKEN_RESOLUTION);
		} catch (Exception e) {
			DebugLog.logException(e);
			ThreadUtils.runOnMainThread(() -> onAuthenticationError(e.getMessage()));
		}
	}

	@Override
	public void dispose() {
		if (googleApiClientConnected) {
			googleApiClient.disconnect();
		}
		executor.shutdown();
		super.dispose();
	}

	@Override
	protected void onAuthenticationStarted() throws AuthenticationException {
		initiateSignIn();
	}

	private void initiateSignIn() {
		googleApiClient.connect();
	}

	@Override
	public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
		boolean processed = false;

		if (requestCode == RC_GOOGLE_SIGNIN_RESOLUTION) {
			if (resultCode == Activity.RESULT_OK) {
				initiateSignIn();
			} else {
				onAuthenticationError(getFragment().getString(R.string.sp_msg_google_signin_failed));
			}
			processed = true;
		} else if (requestCode == RC_GOOGLE_AUTH_TOKEN_RESOLUTION && resultCode == Activity.RESULT_OK) {
			executor.submit(this::obtainGoogleAuthToken);
			processed = true;
		}

		return processed;
	}

	private class GoogleSigninFailListener implements GoogleApiClient.OnConnectionFailedListener {

		@Override
		public void onConnectionFailed(ConnectionResult connectionResult) {
			googleApiClientConnected = false;
			int errorCode = connectionResult.getErrorCode();
			DebugLog.e("connection failure: " + errorCode);
			if (errorCode == ConnectionResult.RESOLUTION_REQUIRED
				|| errorCode == ConnectionResult.SIGN_IN_REQUIRED) {

				try {
					DebugLog.i("starting connection failure resolution");
					connectionResult.startResolutionForResult(getFragment().getActivity(),
						RC_GOOGLE_SIGNIN_RESOLUTION);
				} catch (IntentSender.SendIntentException e) {
					DebugLog.logException(e);
				}
			} else {
				ThreadUtils.runOnMainThread(GoogleNativeAuthenticator.this::onGeneralAuthenticationError);
			}
		}
	}

	private class GoogleSigninConnectionCallbacks implements GoogleApiClient.ConnectionCallbacks {

		@Override
		public void onConnected(Bundle bundle) {
			googleApiClientConnected = true;
			Person currentPerson = Plus.PeopleApi.getCurrentPerson(googleApiClient);
			if (currentPerson != null) {
				executor.submit(GoogleNativeAuthenticator.this::obtainGoogleAuthToken);
			} else {
				onAuthenticationError(getFragment().getString(R.string.sp_msg_couldnt_access_google_profile));
			}
		}

		@Override
		public void onConnectionSuspended(int i) {
			DebugLog.logMethod();
			googleApiClientConnected = false;
		}
	}
}
