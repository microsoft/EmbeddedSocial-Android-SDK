/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.microsoft.socialplus.fetcher.FetchersFactory;
import com.microsoft.socialplus.fetcher.base.Fetcher;
import com.microsoft.socialplus.sdk.R;
import com.microsoft.socialplus.server.model.view.UserCompactView;
import com.microsoft.socialplus.ui.adapter.renderer.BlockedUsersRenderer;
import com.microsoft.socialplus.ui.fragment.base.BaseUsersListFragment;

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
		setEmptyDataMessage(R.string.sp_no_blocked_users);
	}
}
