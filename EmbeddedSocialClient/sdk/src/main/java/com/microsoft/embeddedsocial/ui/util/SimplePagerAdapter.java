/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.util;

import com.microsoft.embeddedsocial.base.function.Producer;

import android.content.Context;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

/**
 * Simple implementation of {@link PagerAdapter}.
 */
public class SimplePagerAdapter extends FragmentPagerAdapter {

    private final Context context;
    private final Page[] items;

    public SimplePagerAdapter(Context context, FragmentManager fm, Page... items) {
        super(fm);
        this.items = items;
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        return items[position].createFragment();
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return context.getString(items[position].getTitleId());
    }

    /**
     * Encapsulates page (tab).
     */
    public static class Page {
        @StringRes
        private final int titleId;
        private final Producer<Fragment> fragmentProducer;

        public Page(int titleId, Producer<Fragment> fragmentProducer) {
            this.titleId = titleId;
            this.fragmentProducer = fragmentProducer;
        }

        @StringRes
        public int getTitleId() {
            return titleId;
        }

        public Fragment createFragment() {
            return fragmentProducer.createNew();
        }
    }
}
