/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.ui.activity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.microsoft.socialplus.base.utils.ViewUtils;
import com.microsoft.socialplus.base.utils.thread.ThreadUtils;
import com.microsoft.socialplus.sdk.R;
import com.microsoft.socialplus.ui.activity.base.BaseActivity;
import com.microsoft.socialplus.ui.fragment.SignInFragment;

import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.TokenResponse;

/**
 * Activity for sign-in.
 */
public class SignInActivity extends BaseActivity {

	private static final String IS_USED = "isUsed";

	public SignInActivity() {
		super(R.id.sp_navigationProfile);
	}

	@Override
	protected void setupFragments() {
		setActivityContent(new SignInFragment());
	}

	@Override
	protected boolean isAuthorizationRequired() {
		return false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		// XXX: keyboard is closed in a handler because of bugs on HTC devices
		ThreadUtils.getMainThreadHandler().post(() -> ViewUtils.hideKeyboard(this));
	}

	public static PendingIntent createPostAuthorizationIntent(@NonNull Context context,
															  @NonNull AuthorizationRequest request) {
		String action = "com.microsoft.socialplus.HANDLE_AUTHORIZATION_RESPONSE";
		Intent intent = new Intent(action);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		return PendingIntent.getActivity(context, request.hashCode(), intent, 0);
	}

	@Override
	protected void onStart() {
		super.onStart();
		checkIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		checkIntent(intent);
	}

	private void checkIntent(Intent intent) {
		if (intent != null) {
			if (intent.getAction().equals("com.microsoft.socialplus.HANDLE_AUTHORIZATION_RESPONSE")) {
				if (!intent.hasExtra(IS_USED)) {
					handleAuthorizationResponse(intent);
					intent.putExtra(IS_USED, true);
				}
			}
		}
	}

	public void handleAuthorizationResponse(Intent intent) {
		AuthorizationResponse resp = AuthorizationResponse.fromIntent(intent);
		AuthorizationException ex = AuthorizationException.fromIntent(intent);
		if (resp != null) {
			System.out.println("Auth succeeded");
			getAccessToken(resp);
		} else {
			System.out.println("Auth failed");
			// authorization failed, check ex for more details
		}
	}

	public void getAccessToken(AuthorizationResponse resp) {
		AuthorizationService service = new AuthorizationService(this);
		service.performTokenRequest(
				resp.createTokenExchangeRequest(),
				new AuthorizationService.TokenResponseCallback() {
					@Override public void onTokenRequestCompleted(
							TokenResponse resp, AuthorizationException ex) {
						if (resp != null) {
							System.out.println("ACCESS TOKEN: ");
							System.out.println(resp.accessToken);
							System.out.println(resp.refreshToken);
							// exchange succeeded
						} else {
							// authorization failed, check ex for more details
						}
					}
				});
	}
}
