/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.microsoft.socialplus.data.model.FollowRequest;
import com.microsoft.socialplus.fetcher.FetchersFactory;
import com.microsoft.socialplus.sdk.R;
import com.microsoft.socialplus.ui.adapter.FetchableListAdapter;
import com.microsoft.socialplus.ui.adapter.renderer.FollowRequestRenderer;
import com.microsoft.socialplus.ui.fragment.base.BaseListContentFragment;
import com.microsoft.socialplus.ui.util.VerticalPaddingDecoration;

/**
 * Shows all follow requests.
 */
public class FollowRequestsFragment extends BaseListContentFragment<FetchableListAdapter<FollowRequest, ?>> {

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		int verticalPadding = getResources().getDimensionPixelOffset(R.dimen.sp_users_list_vertical_padding_large);
		getRecyclerView().addItemDecoration(new VerticalPaddingDecoration(verticalPadding));
		setEmptyDataMessage(R.string.sp_message_no_people);
	}

	@Override
	protected FetchableListAdapter<FollowRequest, ?> createInitialAdapter() {
		return new FetchableListAdapter<>(FetchersFactory.createFollowRequestFetcher(), new FollowRequestRenderer(getContext()));
	}

}
