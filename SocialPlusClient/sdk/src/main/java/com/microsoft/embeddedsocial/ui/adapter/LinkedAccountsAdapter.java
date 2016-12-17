/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.adapter;

import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.microsoft.embeddedsocial.server.model.view.ThirdPartyAccountView;
import com.microsoft.embeddedsocial.autorest.models.IdentityProvider;
import com.microsoft.embeddedsocial.fetcher.base.FetchableAdapter;
import com.microsoft.embeddedsocial.fetcher.base.Fetcher;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.server.model.view.UserAccountView;
import com.microsoft.embeddedsocial.ui.adapter.viewholder.LinkedAccountViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for the linked accounts screen.
 */
public class LinkedAccountsAdapter extends FetchableAdapter<UserAccountView, LinkedAccountViewHolder> {
	private final List<ThirdPartyAccountView> items;
	private SwitchOnClickListener switchOnClickListener;
	private IChangeAccountState changeAccountStateListener;

	public LinkedAccountsAdapter(IChangeAccountState changeAccountStateListener, Fetcher<UserAccountView> fetcher) {
		super(fetcher);
		this.changeAccountStateListener = changeAccountStateListener;
		this.items = new ArrayList<>();
		this.switchOnClickListener = new SwitchOnClickListener();
	}

	public void add(ThirdPartyAccountView value) {
		items.add(value);
	}

	public void clear() {
		items.clear();
	}

	@Override
	public LinkedAccountViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.es_view_account, parent, false);
		return new LinkedAccountViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(LinkedAccountViewHolder holder, int position) {
		holder.renderItem(items.get(position), switchOnClickListener);
	}

	@Override
	public int getItemCount() {
		return items.size();
	}

	public int getCountConnectedAccounts() {
		int connectedAccounts = 0;
		for (ThirdPartyAccountView existAccount : items) {
			if (existAccount.hasAccountHandle()) {
				connectedAccounts++;
			}
		}

		return connectedAccounts;
	}

	public ThirdPartyAccountView getAccountByType(IdentityProvider identityProvider) {
		for (ThirdPartyAccountView account : items) {
			if (account.getIdentityProvider() == identityProvider) {
				return account;
			}
		}

		return null;
	}

	public void updateAccountState(IdentityProvider identityProvider, String accountHandle) {
		ThirdPartyAccountView listAccount = getAccountByType(identityProvider);
		listAccount.setAccountHandle(accountHandle);
		notifyDataSetChanged();
	}

	public interface IChangeAccountState {
		void onChangeAccountState(IdentityProvider identityProvider);
	}

	private class SwitchOnClickListener implements LinkedAccountViewHolder.IOnCheckedListener {
		@Override
		public void onCheckedChanged(SwitchCompat switchCompat, boolean isChecked) {
			changeAccountStateListener.onChangeAccountState((IdentityProvider) switchCompat.getTag());
		}
	}
}
