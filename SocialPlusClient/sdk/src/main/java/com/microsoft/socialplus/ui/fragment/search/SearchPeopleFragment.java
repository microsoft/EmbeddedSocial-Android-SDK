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
import com.microsoft.socialplus.server.model.view.UserCompactView;
import com.microsoft.socialplus.ui.adapter.renderer.Renderer;
import com.microsoft.socialplus.ui.adapter.renderer.UserRenderer;
import com.microsoft.socialplus.ui.adapter.viewholder.UserListItemHolder;
import com.microsoft.socialplus.ui.fragment.base.BaseUsersListFragment;

/**
 * Fragment for people search.
 */
public class SearchPeopleFragment extends BaseUsersListFragment {

	private final SearchModule searchModule = new SearchModule(this, SearchType.PEOPLE);

	private Fetcher<UserCompactView> fetcher;

	public SearchPeopleFragment() {
		addModule(searchModule);
	}

	@Override
	protected Renderer<? super UserCompactView, ? extends UserListItemHolder> createRenderer() {
		return new UserRenderer(getContext());
	}

	@Override
	protected Fetcher<UserCompactView> createFetcher() {
		String query = searchModule.getQuery();
		if (fetcher == null) {
			fetcher = TextUtils.isEmpty(query)
			? FetchersFactory.createDummyFetcher()
			: FetchersFactory.createSearchUsersFetcher(query);
		}
		return fetcher;
	}

	@Override
	public void resetAdapter() {
		fetcher = null;
		super.resetAdapter();
	}
}
