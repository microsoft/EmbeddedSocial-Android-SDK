/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.fragment;

import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.sdk.Options;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.ui.fragment.base.BaseTabsFragment;
import com.microsoft.embeddedsocial.ui.util.SimplePagerAdapter;

import android.support.v4.view.PagerAdapter;

public class RecentActivityFragmentTabs extends BaseTabsFragment {
    @Override
    protected PagerAdapter createPagerAdapter() {
        SimplePagerAdapter.Page userFeed = new SimplePagerAdapter.Page(R.string.es_activity_feed_user, UserActivityFeedFragment::new);
        SimplePagerAdapter.Page followingFeed = new SimplePagerAdapter.Page(R.string.es_activity_feed_following, FollowingActivityFeedFragment::new);

        SimplePagerAdapter.Page[] pages;

        // Only show the following activity feed if user relations are enabled
        if (GlobalObjectRegistry.getObject(Options.class).userRelationsEnabled()) {
            pages = new SimplePagerAdapter.Page[] {userFeed, followingFeed};
        } else { // user relations are not enabled
            pages = new SimplePagerAdapter.Page[] {userFeed};
        }

        return new SimplePagerAdapter(getContext(), getChildFragmentManager(), pages);
    }
}
