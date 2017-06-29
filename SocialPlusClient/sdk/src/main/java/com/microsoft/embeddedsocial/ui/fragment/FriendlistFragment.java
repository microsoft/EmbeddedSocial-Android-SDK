/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.support.annotation.Nullable;

import com.microsoft.embeddedsocial.auth.AbstractAuthenticator;
import com.microsoft.embeddedsocial.auth.GoogleNativeAuthenticator;
import com.microsoft.embeddedsocial.auth.MicrosoftLiveAuthenticator;
import com.microsoft.embeddedsocial.fetcher.FetchersFactory;
import com.microsoft.embeddedsocial.fetcher.base.Fetcher;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.server.model.view.UserCompactView;
import com.microsoft.embeddedsocial.social.AuthorizationRequest;
import com.microsoft.embeddedsocial.social.exception.SocialNetworkException;
import com.microsoft.embeddedsocial.ui.adapter.renderer.UserRenderer;
import com.microsoft.embeddedsocial.ui.fragment.base.BaseUsersListFragment;
import com.microsoft.embeddedsocial.ui.util.SocialNetworkAccount;
import com.microsoft.embeddedsocial.autorest.models.IdentityProvider;
import com.microsoft.embeddedsocial.auth.FacebookAuthenticator;
import com.microsoft.embeddedsocial.auth.IAuthenticationCallback;
import com.microsoft.embeddedsocial.base.utils.EnumUtils;
import com.microsoft.embeddedsocial.base.utils.thread.ThreadUtils;
import com.microsoft.embeddedsocial.service.IntentExtras;
import com.microsoft.embeddedsocial.ui.activity.FriendlistActivity;
import com.microsoft.embeddedsocial.ui.adapter.renderer.Renderer;
import com.microsoft.embeddedsocial.ui.adapter.viewholder.UserListItemHolder;

/**
 * Search users from social networks.
 */
public class FriendlistFragment extends BaseUsersListFragment {

	private IdentityProvider identityProvider;
	private AbstractAuthenticator authenticator;
	private AuthorizationRequestImpl authorizationRequest = new AuthorizationRequestImpl();

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		identityProvider = EnumUtils.getValue(getArguments(), IntentExtras.IDENTITY_PROVIDER, IdentityProvider.class);
	}

	@Override
	protected Renderer<? super UserCompactView, ? extends UserListItemHolder> createRenderer() {
		return new UserRenderer(getContext());
	}

	@Override
	protected Fetcher<UserCompactView> createFetcher() {
		return FetchersFactory.createFriendlistFetcher(identityProvider, authorizationRequest);
	}

	private void showErrorMessage() {
		showToast(R.string.es_msg_general_operation_error);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		boolean activityResultRedirected = data.getBooleanExtra(FriendlistActivity.EXTRA_ACTIVITY_RESULT_REDIRECT, false);
		if (authenticator != null
			&& (!activityResultRedirected || !shouldIgnoreRedirectedActivityResult(authenticator))) {
			authenticator.onActivityResult(requestCode, resultCode, data);
		}
	}

	private static boolean shouldIgnoreRedirectedActivityResult(AbstractAuthenticator authenticator) {
		return authenticator.getAccountType() != IdentityProvider.TWITTER
			&& authenticator.getAccountType() != IdentityProvider.GOOGLE;
	}

	private AbstractAuthenticator getAuthenticator() {
		switch (identityProvider) {
			case FACEBOOK:
				return new FacebookAuthenticator(this, authorizationRequest,
						FacebookAuthenticator.AuthenticationMode.OBTAIN_FRIENDS);

			case GOOGLE:
				return new GoogleNativeAuthenticator(this, authorizationRequest,
						GoogleNativeAuthenticator.AuthenticationMode.OBTAIN_FRIENDS);

			case MICROSOFT:
				return new MicrosoftLiveAuthenticator(this, authorizationRequest,
						MicrosoftLiveAuthenticator.AuthenticationMode.OBTAIN_FRIENDS);

			default:
				throw new RuntimeException("Unknown authentication method");
		}
	}

	@Override
	public boolean onBackPressed() {
		authorizationRequest.cancel();
		return super.onBackPressed();
	}

	/**
	 * Helper class for social network authorization.
	 */
	private class AuthorizationRequestImpl implements AuthorizationRequest, IAuthenticationCallback {

		private final ConditionVariable condition = new ConditionVariable();
		private boolean failed;
		private volatile boolean cancelled;

		@Override
		public void call() throws SocialNetworkException {
			if (cancelled) {
				throw new SocialNetworkException("authorization is cancelled");
			}
			condition.close();
			onLoadingFinished();
			authenticator = getAuthenticator();
			ThreadUtils.runOnMainThread(authenticator::startAuthenticationAsync);
			condition.block();
			if (failed) {
				throw new SocialNetworkException("failed to authorize into a social network");
			}
		}

		@Override
		public void onAuthenticationSuccess(SocialNetworkAccount account) {
			onCompleted(true);
		}

		@Override
		public void onAuthenticationError(String errorMessage) {
			onCompleted(false);
			showErrorMessage();
			finishActivity();
		}

		private void onCompleted(boolean result) {
			authenticator.dispose();
			authenticator = null;
			failed = !result;
			if (result) {
				onLoadingStarted();
			}
			condition.open();
		}

		void cancel() {
			failed = true;
			cancelled = true;
			condition.open();
		}
	}

	public static FriendlistFragment create(IdentityProvider identityProvider) {
		FriendlistFragment fragment = new FriendlistFragment();
		Bundle arguments = new Bundle();
		EnumUtils.putValue(arguments, IntentExtras.IDENTITY_PROVIDER, identityProvider);
		fragment.setArguments(arguments);
		return fragment;
	}

}
