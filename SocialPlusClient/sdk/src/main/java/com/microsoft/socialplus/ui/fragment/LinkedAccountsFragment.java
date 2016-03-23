/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.microsoft.socialplus.autorest.models.IdentityProvider;
import com.microsoft.socialplus.auth.AbstractAuthenticator;
import com.microsoft.socialplus.auth.FacebookAuthenticator;
import com.microsoft.socialplus.auth.GoogleWebAuthenticator;
import com.microsoft.socialplus.auth.IAuthenticationCallback;
import com.microsoft.socialplus.auth.MicrosoftLiveAuthenticator;
import com.microsoft.socialplus.auth.TwitterWebAuthenticator;
import com.microsoft.socialplus.base.utils.thread.ThreadUtils;
import com.microsoft.socialplus.event.LinkUserThirdPartyAccountEvent;
import com.microsoft.socialplus.fetcher.UserAccountFetcher;
import com.microsoft.socialplus.fetcher.base.Callback;
import com.microsoft.socialplus.sdk.R;
import com.microsoft.socialplus.server.model.view.ThirdPartyAccountView;
import com.microsoft.socialplus.service.IntentExtras;
import com.microsoft.socialplus.service.ServiceAction;
import com.microsoft.socialplus.service.WorkerService;
import com.microsoft.socialplus.ui.adapter.LinkedAccountsAdapter;
import com.microsoft.socialplus.ui.dialog.AlertDialogFragment;
import com.microsoft.socialplus.ui.fragment.base.BaseListContentFragment;
import com.microsoft.socialplus.ui.util.SocialNetworkAccount;
import com.squareup.otto.Subscribe;

import java.util.List;

/**
 * Screen with linked accounts state.
 */
public class LinkedAccountsFragment extends BaseListContentFragment<LinkedAccountsAdapter> implements LinkedAccountsAdapter.IChangeAccountState, IAuthenticationCallback {
	private static final String LAST_ACCOUNT_ID = "last_account";

	private final InnerAccountData[] supportedAccountsId = {
		new InnerAccountData(R.string.sp_facebook, IdentityProvider.FACEBOOK),
		new InnerAccountData(R.string.sp_google, IdentityProvider.GOOGLE),
		new InnerAccountData(R.string.sp_microsoft, IdentityProvider.MICROSOFT),
		new InnerAccountData(R.string.sp_twitter, IdentityProvider.TWITTER)
	};

	private AbstractAuthenticator authenticator;
	private UserAccountFetcher fetcher;

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
				.setTitle(R.string.sp_last_account_title)
				.setMessage(R.string.sp_last_account_text)
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
				authenticator = new MicrosoftLiveAuthenticator(this, this);
				break;
			case GOOGLE:
				authenticator = new GoogleWebAuthenticator(this, this);
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
			String error = event.getError();
			if (TextUtils.isEmpty(error)) {
				error = getString(event.getState() == LinkUserThirdPartyAccountEvent.State.LINK
					? R.string.sp_account_link_error
					: R.string.sp_account_unlink_error);
			}
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
			for (int i = 0; i < supportedAccountsId.length; i++) {
				ThirdPartyAccountView existAccount = null;
				for (ThirdPartyAccountView account : accounts) {
					if (account.getIdentityProvider() == supportedAccountsId[i].type) {
						existAccount = account;
						break;
					}
				}
				getAdapter().add(new ThirdPartyAccountView(
					getString(supportedAccountsId[i].nameId),
					(existAccount == null) ? null : existAccount.getAccountHandle(),
					supportedAccountsId[i].type
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
