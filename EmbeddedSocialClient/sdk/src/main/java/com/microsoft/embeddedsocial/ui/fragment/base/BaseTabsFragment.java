/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.fragment.base;

import com.microsoft.embeddedsocial.base.utils.ViewUtils;
import com.microsoft.embeddedsocial.base.view.SlidingTabLayout;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.ui.fragment.FeedViewMenuFragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Base class for fragments with embedded swipe views using tabs as headers
 */
public abstract class BaseTabsFragment extends BaseFragment {
    private ViewPager viewPager;
    private SlidingTabLayout slidingTabLayout;

    public BaseTabsFragment() {
        addThemeToMerge(R.style.EmbeddedSocialSdkThemeOverlayContentFragment);
        addThemeToMerge(R.style.EmbeddedSocialSdkAppTheme_LightBase);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.es_fragment_tabs;
    }

    private final ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            BaseTabsFragment.this.onPageSelected(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    protected void onPageSelected(int position) {
        // do nothing
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // add the list/gallery view options item to the action bar
        getChildFragmentManager().beginTransaction().add(new FeedViewMenuFragment(), FeedViewMenuFragment.TAG).commit();

        viewPager = view.findViewById(R.id.es_viewpager);

        PagerAdapter adapter = createPagerAdapter();
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getCount());

        slidingTabLayout = view.findViewById(R.id.es_slidingTabs);
        slidingTabLayout.setViewPager(viewPager);

        slidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(getContext(), R.color.es_tab_bottom_line));

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
}
