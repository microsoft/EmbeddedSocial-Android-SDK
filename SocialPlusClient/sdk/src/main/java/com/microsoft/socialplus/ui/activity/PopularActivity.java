/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.ui.activity;

import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;

import com.microsoft.socialplus.base.function.Producer;
import com.microsoft.socialplus.data.model.TopicFeedType;
import com.microsoft.socialplus.sdk.R;
import com.microsoft.socialplus.ui.activity.base.BaseTabsActivity;
import com.microsoft.socialplus.ui.fragment.FeedViewMenuFragment;
import com.microsoft.socialplus.ui.fragment.PopularFeedFragment;
import com.microsoft.socialplus.ui.util.SimplePagerAdapter;

/**
 * Activity showing popular feeds.
 */
public class PopularActivity extends BaseTabsActivity {

	public PopularActivity() {
		super(R.id.sp_navigationPopular);
	}

	@Override
	protected void setupFragments() {
		super.setupFragments();
		getSupportFragmentManager().beginTransaction().add(new FeedViewMenuFragment(), FeedViewMenuFragment.TAG).commit();
	}

	@Override
	protected PagerAdapter createPagerAdapter() {
		return new SimplePagerAdapter(this, getSupportFragmentManager(),
			new SimplePagerAdapter.Page(R.string.sp_menu_today, createFragmentProducer(TopicFeedType.EVERYONE_POPULAR_TODAY)),
			new SimplePagerAdapter.Page(R.string.sp_menu_week, createFragmentProducer(TopicFeedType.EVERYONE_POPULAR_THIS_WEEK)),
			new SimplePagerAdapter.Page(R.string.sp_menu_month, createFragmentProducer(TopicFeedType.EVERYONE_POPULAR_THIS_MONTH)),
			new SimplePagerAdapter.Page(R.string.sp_menu_all_time, createFragmentProducer(TopicFeedType.EVERYONE_POPULAR_ALL_TIME))
		);
	}

	private Producer<Fragment> createFragmentProducer(TopicFeedType topicFeedType) {
		return () -> PopularFeedFragment.createForFeedType(topicFeedType);
	}

}
