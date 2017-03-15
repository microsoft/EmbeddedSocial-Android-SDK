/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.fragment;

import com.microsoft.embeddedsocial.fetcher.FetchersFactory;
import com.microsoft.embeddedsocial.server.model.view.TopicView;
import com.microsoft.embeddedsocial.ui.fragment.base.BaseFeedFragment;
import com.microsoft.embeddedsocial.fetcher.base.Fetcher;

/**
 * Shows pinned topics.
 */
public class PinsFragment extends BaseFeedFragment {

	@Override
	protected Fetcher<TopicView> createFetcher() {
		return FetchersFactory.createPinsFeedFetcher();
	}

	@Override
	public void onResume() {
		super.onResume();
		onRefresh();
	}
}
