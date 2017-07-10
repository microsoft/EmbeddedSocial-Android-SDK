/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.fragment;

import com.microsoft.embeddedsocial.account.UserAccount;
import com.microsoft.embeddedsocial.fetcher.FetchersFactory;
import com.microsoft.embeddedsocial.fetcher.base.Fetcher;
import com.microsoft.embeddedsocial.server.model.view.UserCompactView;
import com.microsoft.embeddedsocial.service.IntentExtras;
import com.microsoft.embeddedsocial.ui.adapter.renderer.MyFollowersRenderer;
import com.microsoft.embeddedsocial.ui.adapter.renderer.UserRenderer;

/**
 * Fragment showing user's followers.
 */
public class FollowersFragment extends BaseFollowersFragment {

	@Override
	protected UserRenderer createRenderer() {
		UserAccount userAccount = UserAccount.getInstance();
		String userHandle = getActivity().getIntent().getStringExtra(IntentExtras.USER_HANDLE);
		if (userAccount.isCurrentUser(userHandle)) {
			return new MyFollowersRenderer(getContext());
		} else {
			return new UserRenderer(getContext());
		}
	}

	@Override
	protected Fetcher<UserCompactView> createFetcher() {
		return FetchersFactory.createFollowersFetcher(getUserHandleArgument());
	}

}
