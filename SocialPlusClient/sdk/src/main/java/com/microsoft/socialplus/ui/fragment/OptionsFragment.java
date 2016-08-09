/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.View;

import com.microsoft.socialplus.autorest.models.IdentityProvider;
import com.microsoft.socialplus.account.UserAccount;
import com.microsoft.socialplus.base.GlobalObjectRegistry;
import com.microsoft.socialplus.base.utils.EnumUtils;
import com.microsoft.socialplus.data.Preferences;
import com.microsoft.socialplus.data.storage.DatabaseHelper;
import com.microsoft.socialplus.sdk.R;
import com.microsoft.socialplus.service.IntentExtras;
import com.microsoft.socialplus.service.ServiceAction;
import com.microsoft.socialplus.service.WorkerService;
import com.microsoft.socialplus.ui.activity.DeleteAccountActivity;
import com.microsoft.socialplus.ui.activity.FriendlistActivity;
import com.microsoft.socialplus.ui.activity.LinkedAccountsActivity;
import com.microsoft.socialplus.ui.activity.base.BaseActivity;
import com.microsoft.socialplus.ui.fragment.base.BaseFragment;

/**
 * Settings fragment.
 */
public class OptionsFragment extends BaseFragment {

	@Override
	protected int getLayoutId() {
		return R.layout.sp_fragment_options;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		View.OnClickListener defaultListener = v -> showToast("Not implemented");
		// TODO: hide it in not "master app"
//		setOnClickListener(view, R.id.sp_applications, defaultListener);
//		setOnClickListener(view, R.id.sp_findFacebookFriends, v -> searchFriends(IdentityProvider.FACEBOOK));
//		setOnClickListener(view, R.id.sp_findGooglePlusFriends, v -> searchFriends(IdentityProvider.GOOGLE));
//		setOnClickListener(view, R.id.sp_findMicrosoftContacts, v -> searchFriends(IdentityProvider.MICROSOFT));
//		setOnClickListener(view, R.id.sp_findFriendsFromOtherApps, defaultListener);
		setOnClickListener(view, R.id.sp_privacyPolicy, v -> openWebPage(getString(R.string.sp_privacy_policy_url)));
		setOnClickListener(view, R.id.sp_terms, v -> openWebPage(getString(R.string.sp_terms_url)));
		setOnClickListener(view, R.id.sp_linkedAccounts, v -> startActivity(LinkedAccountsActivity.class));
		setOnClickListener(view, R.id.sp_deleteSearchHistory, v -> deleteSearchHistory());
		setOnClickListener(view, R.id.sp_signOut, v -> signOut());
		setOnClickListener(view, R.id.sp_deleteAccount, v -> startActivity(DeleteAccountActivity.class));
		SwitchCompat lmSwitch = findView(view, R.id.sp_layoutSwitch);
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
		((BaseActivity) getActivity()).disableNavigationPanel();
		showView(R.id.sp_progress);
		hideView(R.id.sp_options);
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
		showToast(R.string.sp_search_history_deleted);
	}

	private void openWebPage(String url) {
		Uri pageUri = Uri.parse(url);
		Intent openPage = new Intent(Intent.ACTION_VIEW, pageUri);
		startActivity(openPage);
	}
}
