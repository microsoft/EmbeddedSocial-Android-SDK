/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.fragment.search;

import com.microsoft.embeddedsocial.fetcher.FetchersFactory;
import com.microsoft.embeddedsocial.fetcher.base.Fetcher;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.ui.adapter.FetchableListAdapter;
import com.microsoft.embeddedsocial.ui.adapter.renderer.TrendingHashtagsRenderer;
import com.microsoft.embeddedsocial.ui.fragment.base.BaseListContentFragment;

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
			.setTitle(getString(R.string.es_trending_hashtags))
			.build();
	}

}
