package com.microsoft.embeddedsocial.ui.fragment;

import com.microsoft.embeddedsocial.base.function.Producer;
import com.microsoft.embeddedsocial.base.utils.EnumUtils;
import com.microsoft.embeddedsocial.base.view.SlidingTabLayout;
import com.microsoft.embeddedsocial.data.model.TopicFeedType;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.service.IntentExtras;
import com.microsoft.embeddedsocial.ui.fragment.base.BaseFragment;
import com.microsoft.embeddedsocial.ui.util.SimplePagerAdapter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class PopularFeedFragmentTabs extends BaseFragment {
    ViewPager viewPager;
    SlidingTabLayout slidingTabLayout;

    private final ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            PopularFeedFragmentTabs.this.onPageSelected(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    public PopularFeedFragmentTabs() {
        addThemeToMerge(R.style.EmbeddedSocialSdkThemeOverlayContentFragment);
        addThemeToMerge(R.style.EmbeddedSocialSdkAppTheme_LightBase);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.es_fragment_tabs;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager = view.findViewById(R.id.es_viewpager);

        PagerAdapter adapter = createPagerAdapter();
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getCount());

        slidingTabLayout = view.findViewById(R.id.es_slidingTabs);
        slidingTabLayout.setViewPager(viewPager);

        slidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(getContext(), R.color.es_tab_bottom_line));

        viewPager.addOnPageChangeListener(onPageChangeListener);
    }

    protected PagerAdapter createPagerAdapter() {
        return new SimplePagerAdapter(getContext(), getActivity().getSupportFragmentManager(),
                new SimplePagerAdapter.Page(R.string.es_menu_today, createFragmentProducer(TopicFeedType.EVERYONE_POPULAR_TODAY)),
                new SimplePagerAdapter.Page(R.string.es_menu_week, createFragmentProducer(TopicFeedType.EVERYONE_POPULAR_THIS_WEEK)),
                new SimplePagerAdapter.Page(R.string.es_menu_month, createFragmentProducer(TopicFeedType.EVERYONE_POPULAR_THIS_MONTH)),
                new SimplePagerAdapter.Page(R.string.es_menu_all_time, createFragmentProducer(TopicFeedType.EVERYONE_POPULAR_ALL_TIME))
        );
    }

    private Producer<Fragment> createFragmentProducer(TopicFeedType topicFeedType) {
        return () -> PopularFeedFragmentTabs.createForFeedType(topicFeedType);
    }

    protected void onPageSelected(int position) {
        // do nothing
    }

    public static PopularFeedFragment createForFeedType(TopicFeedType topicFeedType) {
        PopularFeedFragment fragment = new PopularFeedFragment();
        Bundle arguments = new Bundle();
        EnumUtils.putValue(arguments, IntentExtras.FEED_TYPE, topicFeedType);
        fragment.setArguments(arguments);
        return fragment;
    }
}
