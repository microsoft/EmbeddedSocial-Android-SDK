/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.fragment;

import com.microsoft.embeddedsocial.fetcher.FetchersFactory;
import com.microsoft.embeddedsocial.fetcher.base.Fetcher;
import com.microsoft.embeddedsocial.server.model.view.UserCompactView;

/**
 * Fragment showing people you are following.
 */
public class FollowingFragment extends BaseFollowersFragment {

	@Override
	protected Fetcher<UserCompactView> createFetcher() {
		return FetchersFactory.createFollowingFetcher(getUserHandleArgument());
	}

}
