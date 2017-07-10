/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.microsoft.embeddedsocial.server.model.view.UserCompactView;
import com.microsoft.embeddedsocial.ui.fragment.base.BaseUsersListFragment;
import com.microsoft.embeddedsocial.fetcher.FetchersFactory;
import com.microsoft.embeddedsocial.fetcher.base.Fetcher;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.ui.adapter.renderer.BlockedUsersRenderer;

/**
 * Shows the list of blocked users.
 */
public class BlockedUsersFragment extends BaseUsersListFragment {

	protected BlockedUsersRenderer createRenderer() {
		return new BlockedUsersRenderer(getContext());
	}

	protected Fetcher<UserCompactView> createFetcher() {
		return FetchersFactory.createBlockedUsersFetcher();
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setEmptyDataMessage(R.string.es_no_blocked_users);
	}
}
