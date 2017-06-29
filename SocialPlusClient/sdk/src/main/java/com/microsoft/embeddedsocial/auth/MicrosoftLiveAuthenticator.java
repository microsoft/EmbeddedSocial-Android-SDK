/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.auth;

import android.content.Context;
import android.os.ConditionVariable;
import android.support.v4.app.Fragment;

import com.microsoft.embeddedsocial.base.utils.JsonUtils;
import com.microsoft.embeddedsocial.autorest.models.IdentityProvider;
import com.microsoft.live.LiveAuthClient;
import com.microsoft.live.LiveAuthException;
import com.microsoft.live.LiveAuthListener;
import com.microsoft.live.LiveConnectClient;
import com.microsoft.live.LiveConnectSession;
import com.microsoft.live.LiveOperation;
import com.microsoft.live.LiveOperationException;
import com.microsoft.live.LiveOperationListener;
import com.microsoft.live.LiveStatus;
import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.base.utils.ConnectionUtils;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.data.socialnetwork.MicrosoftLiveProfile;
import com.microsoft.embeddedsocial.sdk.Options;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.ui.util.SocialNetworkAccount;

import java.util.Arrays;
import java.util.List;

/**
 * Implements Microsoft Live authentication process.
 */
public class MicrosoftLiveAuthenticator extends AbstractAuthenticator {
	private final GeneralLiveAuthListener generalLiveAuthListener = new GeneralLiveAuthListener();
	private final LiveOperationListener liveOperationListener = new LiveOperationListenerImpl();
	private final LiveAuthClient authClient;
	private final AuthenticationMode authMode;
	private String liveAccessToken;

	public MicrosoftLiveAuthenticator(Fragment fragment, IAuthenticationCallback authCallback,
									  AuthenticationMode authMode) {
		super(IdentityProvider.MICROSOFT, fragment, authCallback);
		authClient = new LiveAuthClient(
			fragment.getActivity().getApplicationContext(),
			GlobalObjectRegistry.getObject(Options.class).getMicrosoftClientId()
		);
		this.authMode = authMode;
	}

	@Override
	protected void onAuthenticationStarted() throws AuthenticationException {
		authClient.login(getFragment().getActivity(), authMode.getPermissions(), generalLiveAuthListener);
	}

	/**
	 * Signs the user out if he was signed in. <b>Must be called asynchronously.</b>
	 *
	 * @param context valid context
	 * @return the result of sign-out operation.
	 */
	public static boolean signOut(Context context) {
		SignOutLiveAuthListener resultListener = new SignOutLiveAuthListener();
		new LiveAuthClient(context, GlobalObjectRegistry.getObject(Options.class).getMicrosoftClientId())
			.logout(resultListener);

		return resultListener.getResult();
	}

	private class GeneralLiveAuthListener implements LiveAuthListener {
		@Override
		public void onAuthComplete(LiveStatus status, LiveConnectSession session, Object userState) {
			if (status == LiveStatus.CONNECTED) {
				DebugLog.i("Microsoft Live login success");
				if (authMode.canStoreToken()) {
					SocialNetworkTokens.microsoft().storeToken(session);
				}
				liveAccessToken = android.net.Uri.encode(session.getAccessToken());
				LiveConnectClient liveConnectClient = new LiveConnectClient(session);
				liveConnectClient.getAsync("me", liveOperationListener);
			} else {
				onAuthenticationError(
					getFragment().getString(
						R.string.es_message_ms_live_error_pattern,
						getFragment().getString(R.string.es_message_cant_login)
					)
				);
			}
		}

		@Override
		public void onAuthError(LiveAuthException exception, Object userState) {
			if (!ConnectionUtils.isConnectingToInternet(getFragment().getActivity().getBaseContext())) {
				onAuthenticationError(getFragment().getString(R.string.es_message_no_internet_connection));
			} else {
				onAuthenticationError(
					getFragment().getString(R.string.es_message_ms_live_error_pattern, exception.getMessage()));
			}
		}
	}

	private class LiveOperationListenerImpl implements LiveOperationListener {
		@Override
		public void onComplete(LiveOperation operation) {
			MicrosoftLiveProfile profile = JsonUtils.fromJson(operation.getRawResult(), MicrosoftLiveProfile.class);
			if (profile == null) {
				onAuthenticationError(
					getFragment().getString(R.string.es_message_ms_live_error_pattern, getFragment().getString(R.string.es_message_cant_get_profile)));
			} else if (profile.getError() != null) {
				onAuthenticationError(
					getFragment().getString(R.string.es_message_ms_live_error_pattern, profile.getError().getMessage()));
			} else {
				SocialNetworkAccount account = new SocialNetworkAccount(
						IdentityProvider.MICROSOFT,
						profile.getId(),
						liveAccessToken,
						profile.getFirstName(),
						profile.getLastName()
				);
				onAuthenticationSuccess(account);
			}
		}

		@Override
		public void onError(LiveOperationException exception, LiveOperation operation) {
			onAuthenticationError(
				getFragment().getString(R.string.es_message_ms_live_error_pattern, exception.getMessage()));
		}
	}

	private static class SignOutLiveAuthListener implements LiveAuthListener {

		private final ConditionVariable blockingCondition = new ConditionVariable();
		private boolean result;

		@Override
		public void onAuthComplete(LiveStatus status, LiveConnectSession session, Object userState) {
			result = true;
			blockingCondition.open();
		}

		@Override
		public void onAuthError(LiveAuthException exception, Object userState) {
			DebugLog.w("error during sign out: " + exception.getMessage());
			result = false;
			blockingCondition.open();
		}

		boolean getResult() {
			blockingCondition.block();
			return result;
		}
	}

	/**
	 * Microsoft authentication mode.
	 */
	public enum AuthenticationMode {

		/**
		 * Allow sign-in only.
		 */
		SIGN_IN_ONLY(false, "wl.signin"),

		/**
		 * Allow sign-in and obtaining friend list.
		 */
		OBTAIN_FRIENDS(true, "wl.signin", "wl.emails");

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
