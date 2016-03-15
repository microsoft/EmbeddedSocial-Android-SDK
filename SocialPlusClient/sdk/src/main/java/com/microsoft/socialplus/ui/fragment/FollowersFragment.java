/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.ui.fragment;

import com.microsoft.socialplus.fetcher.FetchersFactory;
import com.microsoft.socialplus.fetcher.base.Fetcher;
import com.microsoft.socialplus.server.model.view.UserCompactView;

/**
 * Fragment showing user's followers.
 */
public class FollowersFragment extends BaseFollowersFragment {

	@Override
	protected Fetcher<UserCompactView> createFetcher() {
		return FetchersFactory.createFollowersFetcher(getUserHandleArgument());
	}

}
