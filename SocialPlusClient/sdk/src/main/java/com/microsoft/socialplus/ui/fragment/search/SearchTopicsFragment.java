/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.ui.fragment.search;

import android.text.TextUtils;

import com.microsoft.socialplus.data.model.SearchType;
import com.microsoft.socialplus.fetcher.FetchersFactory;
import com.microsoft.socialplus.fetcher.base.Fetcher;
import com.microsoft.socialplus.server.model.view.TopicView;
import com.microsoft.socialplus.ui.fragment.base.BaseFeedFragment;

/**
 * Fragment showing search results.
 */
public class SearchTopicsFragment extends BaseFeedFragment {

	private final SearchModule searchModule = new SearchModule(this, SearchType.TOPICS);

	public SearchTopicsFragment() {
		addModule(searchModule);
	}

	@Override
	protected Fetcher<TopicView> createFetcher() {
		String query = searchModule.getQuery();
		return TextUtils.isEmpty(query)
			? FetchersFactory.createDummyFetcher()
			: FetchersFactory.createSearchTopicsFetcher(query);
	}

}