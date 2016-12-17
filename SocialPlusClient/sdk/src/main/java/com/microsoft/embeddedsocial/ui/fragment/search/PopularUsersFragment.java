/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.fragment.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.microsoft.embeddedsocial.fetcher.FetchersFactory;
import com.microsoft.embeddedsocial.fetcher.base.Fetcher;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.server.model.view.UserCompactView;
import com.microsoft.embeddedsocial.ui.adapter.FetchableListAdapter;
import com.microsoft.embeddedsocial.ui.adapter.renderer.UserRenderer;
import com.microsoft.embeddedsocial.ui.util.VerticalPaddingDecoration;
import com.microsoft.embeddedsocial.ui.fragment.base.BaseListContentFragment;

/**
 * Shows popular users.
 */
public class PopularUsersFragment extends BaseListContentFragment<FetchableListAdapter<UserCompactView, ?>> {

	private Fetcher<UserCompactView> fetcher;

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		int bottomPadding = view.getResources().getDimensionPixelOffset(R.dimen.es_users_list_vertical_padding);
		getRecyclerView().addItemDecoration(new VerticalPaddingDecoration(bottomPadding, 0));
		setEmptyDataMessage(R.string.es_message_no_people);
	}

	@Override
	protected FetchableListAdapter<UserCompactView, ?> createInitialAdapter() {
		if (fetcher == null) {
			fetcher = FetchersFactory.createPopularUsersFetcher();
		}
		return new FetchableListAdapter.Builder<>(fetcher, new UserRenderer(getContext()))
			.setTitle(getString(R.string.es_suggested_users))
			.build();
	}
}
