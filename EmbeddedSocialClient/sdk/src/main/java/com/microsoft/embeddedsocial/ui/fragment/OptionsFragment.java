/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.ProgressBar;

import com.microsoft.embeddedsocial.account.UserAccount;
import com.microsoft.embeddedsocial.sdk.Options;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.service.WorkerService;
import com.microsoft.embeddedsocial.autorest.models.IdentityProvider;
import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.base.utils.EnumUtils;
import com.microsoft.embeddedsocial.data.Preferences;
import com.microsoft.embeddedsocial.data.storage.DatabaseHelper;
import com.microsoft.embeddedsocial.service.IntentExtras;
import com.microsoft.embeddedsocial.service.ServiceAction;
import com.microsoft.embeddedsocial.ui.activity.DeleteAccountActivity;
import com.microsoft.embeddedsocial.ui.activity.FriendlistActivity;
import com.microsoft.embeddedsocial.ui.activity.LinkedAccountsActivity;
import com.microsoft.embeddedsocial.ui.activity.SignInActivity;
import com.microsoft.embeddedsocial.ui.activity.base.BaseActivity;
import com.microsoft.embeddedsocial.ui.fragment.base.BaseFragment;
import com.microsoft.embeddedsocial.ui.util.WebPageHelper;

/**
 * Settings fragment.
 */
public class OptionsFragment extends BaseFragment {

	@Override
	protected int getLayoutId() {
		return R.layout.es_fragment_options;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		Options options = GlobalObjectRegistry.getObject(Options.class);
		View.OnClickListener defaultListener = v -> showToast("Not implemented");
		// TODO: hide it in not "master app"
//		setOnClickListener(view, R.id.es_applications, defaultListener);
//		setOnClickListener(view, R.id.es_findFacebookFriends, v -> searchFriends(IdentityProvider.FACEBOOK));
//		setOnClickListener(view, R.id.es_findGooglePlusFriends, v -> searchFriends(IdentityProvider.GOOGLE));
//		setOnClickListener(view, R.id.es_findMicrosoftContacts, v -> searchFriends(IdentityProvider.MICROSOFT));
//		setOnClickListener(view, R.id.es_findFriendsFromOtherApps, defaultListener);

		setOnClickListener(view, R.id.es_privacyPolicy, v -> WebPageHelper.openPrivacyPolicy(getContext()));
		setOnClickListener(view, R.id.es_terms, v -> WebPageHelper.openTermsAndConditions(getContext()));

		View signedInOptions = findView(view, R.id.es_optionsSignedIn);
		View signedOutOptions = findView(view, R.id.es_optionsSignedOut);

		if (UserAccount.getInstance().isSignedIn()) {
			// User is signed in
			signedInOptions.setVisibility(View.VISIBLE);
			signedOutOptions.setVisibility(View.GONE);
			setOnClickListener(view, R.id.es_linkedAccounts, v -> startActivity(LinkedAccountsActivity.class));
			if (options.isSearchEnabled()) {
				setOnClickListener(view, R.id.es_deleteSearchHistory, v -> deleteSearchHistory());
			} else {
				View deleteSearchHistoryView = findView(view, R.id.es_deleteSearchHistory);
				deleteSearchHistoryView.setVisibility(View.GONE);
			}
			setOnClickListener(view, R.id.es_signOut, v -> signOut());
			setOnClickListener(view, R.id.es_deleteAccount, v -> startActivity(DeleteAccountActivity.class));
		} else {
			// User is signed out
			signedInOptions.setVisibility(View.GONE);
			signedOutOptions.setVisibility(View.VISIBLE);
			setOnClickListener(view, R.id.es_signIn, v -> startActivity(SignInActivity.class));
		}

		SwitchCompat lmSwitch = findView(view, R.id.es_layoutSwitch);
		if (lmSwitch != null) {
			lmSwitch.setChecked(Preferences.getInstance().getUseStaggeredLayoutManager());
			lmSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> Preferences.getInstance().setUseStaggeredLayoutManager(isChecked));
		}
	}

	private void searchFriends(IdentityProvider identityProvider) {
		Intent intent = new Intent(getContext(), FriendlistActivity.class);
		EnumUtils.putValue(intent, IntentExtras.IDENTITY_PROVIDER, identityProvider);
		startActivity(intent);
	}

	private void signOut() {
		Activity activity = getActivity();
		if (activity instanceof BaseActivity) {
			((BaseActivity)activity).disableNavigationPanel();
		}
		ProgressBar progressBar = findView(getView(), R.id.es_progress);
		progressBar.setVisibility(View.VISIBLE);
		hideView(R.id.es_options);
		UserAccount.getInstance().signOut();
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				GlobalObjectRegistry.getObject(DatabaseHelper.class).clearData();
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				updateActivitiesStackOnLogOut();
			}
		}.execute();
	}

	private void deleteSearchHistory() {
		WorkerService.getLauncher(getContext()).launchService(ServiceAction.DELETE_SEARCH_HISTORY);
		showToast(R.string.es_search_history_deleted);
	}
}
