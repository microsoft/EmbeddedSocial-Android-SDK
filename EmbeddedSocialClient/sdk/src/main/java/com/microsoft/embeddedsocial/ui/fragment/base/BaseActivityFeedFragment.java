/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.fragment.base;

import com.microsoft.embeddedsocial.data.display.DisplayMethod;
import com.microsoft.embeddedsocial.fetcher.base.FetchableAdapter;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.server.model.view.ActivityView;
import com.microsoft.embeddedsocial.ui.util.VerticalPaddingDecoration;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * Base class for fragments showing an activity feed.
 */
public abstract class BaseActivityFeedFragment extends BaseListContentFragment<FetchableAdapter<ActivityView, ?>> {

    protected BaseActivityFeedFragment() {
        addThemeToMerge(R.style.EmbeddedSocialSdkThemeOverlayFeed);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setSidePadding(DisplayMethod.LIST.getHorizontalPadding(getContext())); // TODO: check if it's needed
        int padding = getResources().getDimensionPixelOffset(R.dimen.es_card_list_padding_ver);
        getRecyclerView().addItemDecoration(new VerticalPaddingDecoration(padding));
    }
}
