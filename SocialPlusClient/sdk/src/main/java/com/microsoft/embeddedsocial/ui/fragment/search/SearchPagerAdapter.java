/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.fragment.search;

import android.content.Context;
import android.support.v4.app.FragmentManager;

import com.microsoft.embeddedsocial.data.model.SearchType;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.ui.activity.SearchActivity;
import com.microsoft.embeddedsocial.ui.util.SimplePagerAdapter;

/**
 * {@link android.support.v4.view.PagerAdapter} implementation for {@link SearchActivity}.
 */
public class SearchPagerAdapter extends SimplePagerAdapter {

	private static final int TRENDING_HASHTAGS_ID = 0;
	private static final int SEARCH_TOPICS_ID = 1;
	private static final int POPULAR_USERS_ID = 2;
	private static final int SEARCH_PEOPLE_ID = 3;

	private SearchTextHolder searchTextHolder;

	public SearchPagerAdapter(Context context, FragmentManager fm, SearchTextHolder searchTextHolder) {
		super(context, fm, new SimplePagerAdapter.Page(
				R.string.es_tab_topics,
				() -> searchTextHolder.isSearchTextEmpty(SearchType.TOPICS)
					? new TrendingHashtagsFragment()
					: new SearchTopicsFragment()
			),
			new SimplePagerAdapter.Page(
				R.string.es_tab_people,
				() -> searchTextHolder.isSearchTextEmpty(SearchType.PEOPLE)
					? new PopularUsersFragment()
					: new SearchPeopleFragment()
			)
		);
		this.searchTextHolder = searchTextHolder;
	}

	@Override
	public int getItemPosition(Object object) {
		Class<?> fragmentClass = object.getClass();
		return (fragmentClass.equals(TrendingHashtagsFragment.class) && !searchTextHolder.isSearchTextEmpty(SearchType.TOPICS))
			|| (fragmentClass.equals(PopularUsersFragment.class) && !searchTextHolder.isSearchTextEmpty(SearchType.PEOPLE))
			? POSITION_NONE
			: POSITION_UNCHANGED;
	}

	@Override
	public long getItemId(int position) {
		if (position == 0) {
			return searchTextHolder.isSearchTextEmpty(SearchType.TOPICS) ? TRENDING_HASHTAGS_ID : SEARCH_TOPICS_ID;
		} else {
			return searchTextHolder.isSearchTextEmpty(SearchType.PEOPLE) ? POPULAR_USERS_ID : SEARCH_PEOPLE_ID;
		}
	}

}
