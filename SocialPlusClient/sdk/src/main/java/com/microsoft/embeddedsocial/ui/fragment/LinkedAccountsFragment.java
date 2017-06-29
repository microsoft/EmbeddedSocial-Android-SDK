/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.microsoft.embeddedsocial.auth.AbstractAuthenticator;
import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.fetcher.UserAccountFetcher;
import com.microsoft.embeddedsocial.sdk.Options;
import com.microsoft.embeddedsocial.server.exception.ConflictException;
import com.microsoft.embeddedsocial.server.model.view.ThirdPartyAccountView;
import com.microsoft.embeddedsocial.ui.adapter.LinkedAccountsAdapter;
import com.microsoft.embeddedsocial.autorest.models.IdentityProvider;
import com.microsoft.embeddedsocial.auth.FacebookAuthenticator;
import com.microsoft.embeddedsocial.auth.GoogleNativeAuthenticator;
import com.microsoft.embeddedsocial.auth.IAuthenticationCallback;
import com.microsoft.embeddedsocial.auth.MicrosoftLiveAuthenticator;
import com.microsoft.embeddedsocial.auth.TwitterWebAuthenticator;
import com.microsoft.embeddedsocial.base.utils.thread.ThreadUtils;
import com.microsoft.embeddedsocial.event.LinkUserThirdPartyAccountEvent;
import com.microsoft.embeddedsocial.fetcher.base.Callback;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.service.IntentExtras;
import com.microsoft.embeddedsocial.service.ServiceAction;
import com.microsoft.embeddedsocial.service.WorkerService;
import com.microsoft.embeddedsocial.ui.dialog.AlertDialogFragment;
import com.microsoft.embeddedsocial.ui.fragment.base.BaseListContentFragment;
import com.microsoft.embeddedsocial.ui.util.SocialNetworkAccount;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Screen with linked accounts state.
 */
public class LinkedAccountsFragment extends BaseListContentFragment<LinkedAccountsAdapter> implements LinkedAccountsAdapter.IChangeAccountState, IAuthenticationCallback {
	private static final String LAST_ACCOUNT_ID = "last_account";

	private final List<InnerAccountData> supportedAccountsId;

	private AbstractAuthenticator authenticator;
	private UserAccountFetcher fetcher;

	public LinkedAccountsFragment() {
		Options options = GlobalObjectRegistry.getObject(Options.class);
		supportedAccountsId = new ArrayList<>(4);

		if (options.isFacebookLoginEnabled()) {
			supportedAccountsId.add(new InnerAccountData(R.string.es_facebook, IdentityProvider.FACEBOOK));
		}
		if (options.isGoogleLoginEnabled()) {
			supportedAccountsId.add(new InnerAccountData(R.string.es_google, IdentityProvider.GOOGLE));
		}
		if (options.isMicrosoftLoginEnabled()) {
			supportedAccountsId.add(new InnerAccountData(R.string.es_microsoft, IdentityProvider.MICROSOFT));
		}
		if (options.isTwitterLoginEnabled()) {
			supportedAccountsId.add(new InnerAccountData(R.string.es_twitter, IdentityProvider.TWITTER));
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (authenticator != null) {
			authenticator.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	protected LinkedAccountsAdapter createInitialAdapter() {
		if (fetcher == null) {
			fetcher = new UserAccountFetcher();
		}
		final LinkedAccountsAdapter adapter = new LinkedAccountsAdapter(this, fetcher);
		adapter.addFetcherCallback(new DataRequestSucceededCallback());
		return adapter;
	}

	@Override
	public void onChangeAccountState(IdentityProvider identityProvider) {
		ThirdPartyAccountView account = getAdapter().getAccountByType(identityProvider);

		if (account.hasAccountHandle()) {
			unlinkAccount(identityProvider);
		} else {
			linkAccount(identityProvider);
		}
	}

	private void unlinkAccount(IdentityProvider identityProvider) {
		if (getAdapter().getCountConnectedAccounts() == 1) {
			new AlertDialogFragment.Builder(getActivity(), LAST_ACCOUNT_ID)
				.setTitle(R.string.es_last_account_title)
				.setMessage(R.string.es_last_account_text)
				.setPositiveButton(android.R.string.ok)
				.show(getActivity(), null);
			getAdapter().notifyDataSetChanged();
		} else {
			Bundle extras = new Bundle();
			extras.putString(IntentExtras.IDENTITY_PROVIDER, identityProvider.toValue());
			WorkerService.getLauncher(getContext()).launchService(ServiceAction.UNLINK_USER_THIRD_PARTY_ACCOUNT, extras);
		}
	}

	private void linkAccount(IdentityProvider identityProvider) {
		switch (identityProvider) {
			case FACEBOOK:
				authenticator = new FacebookAuthenticator(this, this, FacebookAuthenticator.AuthenticationMode.SIGN_IN_ONLY);
				break;
			case MICROSOFT:
				authenticator = new MicrosoftLiveAuthenticator(this, this, MicrosoftLiveAuthenticator.AuthenticationMode.SIGN_IN_ONLY);
				break;
			case GOOGLE:
				authenticator = new GoogleNativeAuthenticator(this, this, GoogleNativeAuthenticator.AuthenticationMode.SIGN_IN_ONLY);
				break;
			case TWITTER:
				authenticator = new TwitterWebAuthenticator(this, this);
				break;
		}
		authenticator.startAuthenticationAsync();
	}

	@Override
	public void onAuthenticationSuccess(SocialNetworkAccount account) {
		ThreadUtils.runOnMainThread(() -> {
			onAuthenticationCompleted();
			Bundle extras = new Bundle();
			extras.putParcelable(IntentExtras.SOCIAL_NETWORK_ACCOUNT, account);
			WorkerService.getLauncher(getContext()).launchService(ServiceAction.LINK_USER_THIRD_PARTY_ACCOUNT, extras);
		});
	}

	@Override
	public void onAuthenticationError(String errorMessage) {
		ThreadUtils.runOnMainThread(() -> {
			onAuthenticationCompleted();
			Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
			refreshSilentlyIfNeeded();
		});
	}

	private void onAuthenticationCompleted() {
		authenticator.dispose();
		authenticator = null;
	}

	@Subscribe
	public void onLinkUserThirdPartyAccount(LinkUserThirdPartyAccountEvent event) {
		if (event.iSuccess()) {
			if (event.getState() == LinkUserThirdPartyAccountEvent.State.LINK) {
				getAdapter().updateAccountState(
					event.getAccount().getAccountType(),
					event.getAccount().getThirdPartyAccountHandle());
			} else {
				getAdapter().updateAccountState(event.getIdentityProvider(), "");
			}
		} else {
			int errorMessage;
			if (event.getState() == LinkUserThirdPartyAccountEvent.State.LINK) {
				if (event.getStatusCode() == ConflictException.STATUS_CODE) {
					errorMessage = R.string.es_account_link_conflict;
				} else {
					errorMessage = R.string.es_account_link_error;
				}
			} else {
				errorMessage = R.string.es_account_unlink_error;
			}
			String error = getString(errorMessage);
			getAdapter().notifyDataSetChanged();
			showToast(error);
		}
	}

	private class DataRequestSucceededCallback extends Callback {
		@Override
		public void onDataRequestSucceeded() {
			final List<ThirdPartyAccountView> accounts =
				fetcher.getAllData().get(0).getThirdPartyAccounts();

			getAdapter().clear();
			for (InnerAccountData accountData : supportedAccountsId) {
				ThirdPartyAccountView existAccount = null;
				for (ThirdPartyAccountView account : accounts) {
					if (account.getIdentityProvider() == accountData.type) {
						existAccount = account;
						break;
					}
				}
				getAdapter().add(new ThirdPartyAccountView(
					getString(accountData.nameId),
					(existAccount == null) ? null : existAccount.getAccountHandle(),
						accountData.type
				));
			}
		}
	}

	private class InnerAccountData {
		int nameId;
		IdentityProvider type;

		public InnerAccountData(int nameId, IdentityProvider type) {
			this.nameId = nameId;
			this.type = type;
		}
	}
}
