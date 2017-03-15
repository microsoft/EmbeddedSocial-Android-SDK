/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.activity.base;

import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.microsoft.embeddedsocial.base.utils.ViewUtils;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.base.view.SlidingTabLayout;

/**
 * Base class for activities with tabs.
 */
public abstract class BaseTabsActivity extends BaseActivity {

	private final ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		}

		@Override
		public void onPageSelected(int position) {
			BaseTabsActivity.this.onPageSelected(position);
		}

		@Override
		public void onPageScrollStateChanged(int state) {
		}
	};

	private ViewPager viewPager;
	private SlidingTabLayout slidingTabLayout;

	protected BaseTabsActivity() {
	}

	protected BaseTabsActivity(int activeNavigationItemId) {
		super(activeNavigationItemId);
	}

	@Override
	protected void setupLayout() {
		setActivityContent(R.layout.es_fragment_tabs);
		viewPager = (ViewPager) findViewById(R.id.es_viewpager);

		PagerAdapter adapter = createPagerAdapter();
		viewPager.setAdapter(adapter);
		viewPager.setOffscreenPageLimit(adapter.getCount());

		slidingTabLayout = (SlidingTabLayout) findViewById(R.id.es_slidingTabs);
		slidingTabLayout.setViewPager(viewPager);

		slidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(this, R.color.es_tab_bottom_line));

		viewPager.addOnPageChangeListener(onPageChangeListener);
	}

	/**
	 * Creates an adapter for {@link ViewPager}.
	 */
	protected abstract PagerAdapter createPagerAdapter();

	public int getCurrentPagePosition() {
		return viewPager.getCurrentItem();
	}

	/**
	 * Set whether the tabs indicator is visible (we hide it when there is only one tab).
	 */
	protected void setTabsIndicatorVisible(boolean visible) {
		ViewUtils.setVisible(slidingTabLayout, visible);
	}

	protected void notifyTabsChanged() {
		if (viewPager != null && slidingTabLayout != null) {
			PagerAdapter adapter = viewPager.getAdapter();
			adapter.notifyDataSetChanged();
			viewPager.setOffscreenPageLimit(adapter.getCount());
			slidingTabLayout.setViewPager(viewPager);
		}
	}

	protected void onPageSelected(int position) {
		// do nothing
	}
}
