/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.ui.fragment.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.microsoft.socialplus.data.display.DisplayMethod;
import com.microsoft.socialplus.fetcher.base.FetchableAdapter;
import com.microsoft.socialplus.sdk.R;
import com.microsoft.socialplus.server.model.view.ActivityView;
import com.microsoft.socialplus.ui.util.VerticalPaddingDecoration;

/**
 * Base class for fragments showing an activity feed.
 */
public abstract class BaseActivityFeedFragment extends BaseListContentFragment<FetchableAdapter<ActivityView, ?>> {

	protected BaseActivityFeedFragment() {
		addThemeToMerge(R.style.SocialPlusSdkThemeOverlayFeed);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setSidePadding(DisplayMethod.LIST.getHorizontalPadding(getContext())); // TODO: check if it's needed
		int padding = getResources().getDimensionPixelOffset(R.dimen.sp_card_list_padding_ver);
		getRecyclerView().addItemDecoration(new VerticalPaddingDecoration(padding));
	}
}
