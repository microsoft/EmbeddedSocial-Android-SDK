/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.util;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.microsoft.embeddedsocial.base.function.Producer;

/**
 * Simple implementation of {@link android.support.v4.view.PagerAdapter}.
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
