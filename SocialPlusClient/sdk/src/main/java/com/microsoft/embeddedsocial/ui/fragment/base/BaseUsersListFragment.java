/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.fragment.base;

import android.os.Bundle;
import android.support.annotation.DimenRes;
import android.support.annotation.Nullable;
import android.view.View;

import com.microsoft.embeddedsocial.fetcher.base.Fetcher;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.server.model.view.UserCompactView;
import com.microsoft.embeddedsocial.ui.adapter.FetchableListAdapter;
import com.microsoft.embeddedsocial.ui.adapter.renderer.Renderer;
import com.microsoft.embeddedsocial.ui.adapter.viewholder.UserListItemHolder;
import com.microsoft.embeddedsocial.ui.util.VerticalPaddingDecoration;

/**
 * Base class for fragments showing users list.
 */
public abstract class BaseUsersListFragment extends BaseListContentFragment<FetchableListAdapter<UserCompactView, ?>> {

	@DimenRes
	private final int verticalPaddingRes;

	protected BaseUsersListFragment() {
		this(R.dimen.es_users_list_vertical_padding);
	}

	protected BaseUsersListFragment(@DimenRes int verticalPaddingRes) {
		this.verticalPaddingRes = verticalPaddingRes;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		int verticalPadding = view.getResources().getDimensionPixelOffset(verticalPaddingRes);
		getRecyclerView().addItemDecoration(new VerticalPaddingDecoration(verticalPadding));
		setEmptyDataMessage(R.string.es_message_no_people);
	}

	@Override
	protected FetchableListAdapter<UserCompactView, ?> createInitialAdapter() {
		return new FetchableListAdapter<>(createFetcher(), createRenderer());
	}

	protected abstract Renderer<? super UserCompactView, ? extends UserListItemHolder> createRenderer();

	protected abstract Fetcher<UserCompactView> createFetcher();

}
