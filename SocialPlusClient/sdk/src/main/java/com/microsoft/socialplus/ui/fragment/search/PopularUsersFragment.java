/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.ui.fragment.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.microsoft.socialplus.fetcher.FetchersFactory;
import com.microsoft.socialplus.fetcher.base.Fetcher;
import com.microsoft.socialplus.sdk.R;
import com.microsoft.socialplus.server.model.view.UserCompactView;
import com.microsoft.socialplus.ui.adapter.FetchableListAdapter;
import com.microsoft.socialplus.ui.adapter.renderer.UserRenderer;
import com.microsoft.socialplus.ui.fragment.base.BaseListContentFragment;
import com.microsoft.socialplus.ui.util.VerticalPaddingDecoration;

/**
 * Shows popular users.
 */
public class PopularUsersFragment extends BaseListContentFragment<FetchableListAdapter<UserCompactView, ?>> {

	private Fetcher<UserCompactView> fetcher;

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		int bottomPadding = view.getResources().getDimensionPixelOffset(R.dimen.sp_users_list_vertical_padding);
		getRecyclerView().addItemDecoration(new VerticalPaddingDecoration(bottomPadding, 0));
		setEmptyDataMessage(R.string.sp_message_no_people);
	}

	@Override
	protected FetchableListAdapter<UserCompactView, ?> createInitialAdapter() {
		if (fetcher == null) {
			fetcher = FetchersFactory.createPopularUsersFetcher();
		}
		return new FetchableListAdapter.Builder<>(fetcher, new UserRenderer(getContext()))
			.setTitle(getString(R.string.sp_suggested_users))
			.build();
	}
}
