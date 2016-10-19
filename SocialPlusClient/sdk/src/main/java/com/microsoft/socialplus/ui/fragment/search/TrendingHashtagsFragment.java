/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.ui.fragment.search;

import com.microsoft.socialplus.fetcher.FetchersFactory;
import com.microsoft.socialplus.fetcher.base.Fetcher;
import com.microsoft.socialplus.sdk.R;
import com.microsoft.socialplus.ui.adapter.FetchableListAdapter;
import com.microsoft.socialplus.ui.adapter.renderer.TrendingHashtagsRenderer;
import com.microsoft.socialplus.ui.fragment.base.BaseListContentFragment;

/**
 * Shows the list of trending hashtags.
 */
public class TrendingHashtagsFragment extends BaseListContentFragment<FetchableListAdapter<?, ?>> {

	private Fetcher<String> fetcher;

	@Override
	protected FetchableListAdapter<?, ?> createInitialAdapter() {
		if (fetcher == null) {
			fetcher = FetchersFactory.createTrendingHashtagsFetcher();
		}
		return new FetchableListAdapter.Builder<>(fetcher, new TrendingHashtagsRenderer())
			.setTitle(getString(R.string.sp_trending_hashtags))
			.build();
	}

}
