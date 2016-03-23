/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.auth;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.microsoft.socialplus.autorest.models.IdentityProvider;
import com.microsoft.socialplus.base.GlobalObjectRegistry;
import com.microsoft.socialplus.base.utils.debug.DebugLog;
import com.microsoft.socialplus.sdk.Options;
import com.microsoft.socialplus.ui.activity.WebAuthenticationActivity;
import com.microsoft.socialplus.ui.util.SocialNetworkAccount;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Implements Google authentication process using web view instead of native UX.
 */
public class GoogleWebAuthenticator extends AbstractAuthenticator {

	private static final Uri BASE_AUTH_URI = Uri.parse("https://accounts.google.com/o/oauth2/auth");
	private static final String END_AUTH_URL = "https://accounts.google.com/o/oauth2/approval?";

	private static final String PARAM_CLIENT_ID = "client_id";
	private static final String PARAM_SCOPE = "scope";
	private static final String PARAM_REDIRECT_URI = "redirect_uri";
	private static final String PARAM_RESPONSE_TYPE = "response_type";
	private static final String PARAM_STATE = "state";
	private static final String CODE_PARAM_PREFIX = "code=";

	private static final String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";
	private static final String DEFAULT_SCOPE = "openid";
	private static final String DEFAULT_RESPONSE_TYPE = "code";
	private static final String DEFAULT_STATE = "0x1";

	private static final int RC_WEB_AUTH_REQUEST = 1000;

	public GoogleWebAuthenticator(Fragment fragment, IAuthenticationCallback authCallback) {
		super(IdentityProvider.GOOGLE, fragment, authCallback);
	}

	private String buildAuthUrl() {
		return BASE_AUTH_URI.buildUpon()
			.appendQueryParameter(PARAM_CLIENT_ID, GlobalObjectRegistry.getObject(Options.class).getGoogleClientId())
			.appendQueryParameter(PARAM_REDIRECT_URI, REDIRECT_URI)
			.appendQueryParameter(PARAM_RESPONSE_TYPE, DEFAULT_RESPONSE_TYPE)
			.appendQueryParameter(PARAM_STATE, DEFAULT_STATE)
			.appendQueryParameter(PARAM_SCOPE, DEFAULT_SCOPE)
			.toString();
	}

	@Override
	protected void onAuthenticationStarted() throws AuthenticationException {
		Intent authIntent = new Intent(getFragment().getActivity(), WebAuthenticationActivity.class)
			.putExtra(WebAuthenticationActivity.EXTRA_AUTH_URL, buildAuthUrl())
			.putExtra(WebAuthenticationActivity.EXTRA_END_URL, END_AUTH_URL)
			.putExtra(WebAuthenticationActivity.EXTRA_AUTH_MODE,
				WebAuthenticationActivity.AuthMode.WAIT_FOR_END_URL_RETURN_TITLE.ordinal());
		getFragment().startActivityForResult(authIntent, RC_WEB_AUTH_REQUEST);
	}

	@Override
	public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RC_WEB_AUTH_REQUEST) {
			if (resultCode == Activity.RESULT_OK) {
				onResultUrlObtained(data.getStringExtra(WebAuthenticationActivity.EXTRA_RESULT_URL));
			} else {
				onGeneralAuthenticationError();
			}
			return true;
		} else {
			return false;
		}
	}

	private void onResultUrlObtained(String resultUrl) {
		String[] params = resultUrl.split("&");

		String authCode = null;
		for (String paramPair : params) {
			if (paramPair.startsWith(CODE_PARAM_PREFIX)) {
				authCode = getAuthCodeFromQueryParameter(paramPair);
			}
		}

		if (TextUtils.isEmpty(authCode)) {
			onGeneralAuthenticationError();
		} else {
			onAuthenticationSuccess(
					SocialNetworkAccount.fromOauthCode(IdentityProvider.GOOGLE, authCode));
		}
	}

	private String getAuthCodeFromQueryParameter(String paramPair) {
		String authCode = null;
		String[] paramParts = paramPair.split("=");

		if (paramParts.length > 1) {
			try {
				authCode = URLDecoder.decode(paramParts[1], "UTF-8");
			} catch (UnsupportedEncodingException e) {
				DebugLog.logException(e);
			}
		}

		return authCode;
	}
}
