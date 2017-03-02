/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.microsoft.embeddedsocial.fetcher.FetchersFactory;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.ui.adapter.FetchableListAdapter;
import com.microsoft.embeddedsocial.ui.adapter.renderer.FollowRequestRenderer;
import com.microsoft.embeddedsocial.ui.fragment.base.BaseListContentFragment;
import com.microsoft.embeddedsocial.ui.util.VerticalPaddingDecoration;
import com.microsoft.embeddedsocial.data.model.FollowRequest;

/**
 * Shows all follow requests.
 */
public class FollowRequestsFragment extends BaseListContentFragment<FetchableListAdapter<FollowRequest, ?>> {

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		int verticalPadding = getResources().getDimensionPixelOffset(R.dimen.es_users_list_vertical_padding_large);
		getRecyclerView().addItemDecoration(new VerticalPaddingDecoration(verticalPadding));
		setEmptyDataMessage(R.string.es_message_no_people);
	}

	@Override
	protected FetchableListAdapter<FollowRequest, ?> createInitialAdapter() {
		return new FetchableListAdapter<>(FetchersFactory.createFollowRequestFetcher(), new FollowRequestRenderer(getContext()));
	}

}
