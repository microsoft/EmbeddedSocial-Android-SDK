/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.fragment;

import com.microsoft.embeddedsocial.base.function.Producer;
import com.microsoft.embeddedsocial.base.utils.EnumUtils;
import com.microsoft.embeddedsocial.data.model.TopicFeedType;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.service.IntentExtras;
import com.microsoft.embeddedsocial.ui.fragment.base.BaseTabsFragment;
import com.microsoft.embeddedsocial.ui.util.SimplePagerAdapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;

public class PopularFeedFragmentTabs extends BaseTabsFragment {
    protected PagerAdapter createPagerAdapter() {
        return new SimplePagerAdapter(getContext(), getChildFragmentManager(),
                new SimplePagerAdapter.Page(R.string.es_menu_today, createFragmentProducer(TopicFeedType.EVERYONE_POPULAR_TODAY)),
                new SimplePagerAdapter.Page(R.string.es_menu_week, createFragmentProducer(TopicFeedType.EVERYONE_POPULAR_THIS_WEEK)),
                new SimplePagerAdapter.Page(R.string.es_menu_month, createFragmentProducer(TopicFeedType.EVERYONE_POPULAR_THIS_MONTH)),
                new SimplePagerAdapter.Page(R.string.es_menu_all_time, createFragmentProducer(TopicFeedType.EVERYONE_POPULAR_ALL_TIME))
        );
    }

    private Producer<Fragment> createFragmentProducer(TopicFeedType topicFeedType) {
        return () -> PopularFeedFragmentTabs.createForFeedType(topicFeedType);
    }

    public static PopularFeedFragment createForFeedType(TopicFeedType topicFeedType) {
        PopularFeedFragment fragment = new PopularFeedFragment();
        Bundle arguments = new Bundle();
        EnumUtils.putValue(arguments, IntentExtras.FEED_TYPE, topicFeedType);
        fragment.setArguments(arguments);
        return fragment;
    }
}
