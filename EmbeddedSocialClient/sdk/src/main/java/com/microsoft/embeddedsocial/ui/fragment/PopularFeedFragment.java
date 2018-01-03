/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.fragment;

import com.microsoft.embeddedsocial.base.function.Producer;
import com.microsoft.embeddedsocial.data.model.TopicFeedType;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.ui.fragment.base.BaseTabsFragment;
import com.microsoft.embeddedsocial.ui.util.SimplePagerAdapter;

import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;

/**
 * Fragment showing the popular feeds displayed as tabbed views
 */
public class PopularFeedFragment extends BaseTabsFragment {
    public static final String TAG = "PopularFeedFragment";

    protected PagerAdapter createPagerAdapter() {
        return new SimplePagerAdapter(getContext(), getChildFragmentManager(),
                new SimplePagerAdapter.Page(R.string.es_menu_today, createFragmentProducer(TopicFeedType.EVERYONE_POPULAR_TODAY)),
                new SimplePagerAdapter.Page(R.string.es_menu_week, createFragmentProducer(TopicFeedType.EVERYONE_POPULAR_THIS_WEEK)),
                new SimplePagerAdapter.Page(R.string.es_menu_month, createFragmentProducer(TopicFeedType.EVERYONE_POPULAR_THIS_MONTH)),
                new SimplePagerAdapter.Page(R.string.es_menu_all_time, createFragmentProducer(TopicFeedType.EVERYONE_POPULAR_ALL_TIME))
        );
    }

    private Producer<Fragment> createFragmentProducer(TopicFeedType topicFeedType) {
        return () -> PopularFeedFragmentTab.createForFeedType(topicFeedType);
    }
}
