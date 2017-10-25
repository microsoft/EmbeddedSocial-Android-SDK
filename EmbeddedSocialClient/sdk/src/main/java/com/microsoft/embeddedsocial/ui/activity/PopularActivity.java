/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.activity;

import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;

import com.microsoft.embeddedsocial.ui.fragment.FeedViewMenuFragment;
import com.microsoft.embeddedsocial.base.function.Producer;
import com.microsoft.embeddedsocial.data.model.TopicFeedType;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.ui.activity.base.BaseTabsActivity;
import com.microsoft.embeddedsocial.ui.fragment.PopularFeedFragment;
import com.microsoft.embeddedsocial.ui.util.SimplePagerAdapter;

/**
 * Activity showing popular feeds.
 */
public class PopularActivity extends BaseTabsActivity {
	public static final String NAME = "Popular";

	public PopularActivity() {
		super(R.id.es_navigationPopular);
	}

	@Override
	protected void setupFragments() {
		super.setupFragments();
		getSupportFragmentManager().beginTransaction().add(new FeedViewMenuFragment(), FeedViewMenuFragment.TAG).commit();
	}

	@Override
	protected PagerAdapter createPagerAdapter() {
		return new SimplePagerAdapter(this, getSupportFragmentManager(),
			new SimplePagerAdapter.Page(R.string.es_menu_today, createFragmentProducer(TopicFeedType.EVERYONE_POPULAR_TODAY)),
			new SimplePagerAdapter.Page(R.string.es_menu_week, createFragmentProducer(TopicFeedType.EVERYONE_POPULAR_THIS_WEEK)),
			new SimplePagerAdapter.Page(R.string.es_menu_month, createFragmentProducer(TopicFeedType.EVERYONE_POPULAR_THIS_MONTH)),
			new SimplePagerAdapter.Page(R.string.es_menu_all_time, createFragmentProducer(TopicFeedType.EVERYONE_POPULAR_ALL_TIME))
		);
	}

	private Producer<Fragment> createFragmentProducer(TopicFeedType topicFeedType) {
		return () -> PopularFeedFragment.createForFeedType(topicFeedType);
	}

	@Override
	protected String getName() {
		return NAME;
	}
}
