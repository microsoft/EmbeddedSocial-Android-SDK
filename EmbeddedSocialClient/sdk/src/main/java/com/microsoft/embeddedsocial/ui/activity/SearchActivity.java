/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.activity;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ContentProviderClient;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.base.event.EventBus;
import com.microsoft.embeddedsocial.base.utils.ViewUtils;
import com.microsoft.embeddedsocial.data.model.SearchType;
import com.microsoft.embeddedsocial.data.storage.SearchHistory;
import com.microsoft.embeddedsocial.event.click.OnTrendingHashtagSelectedEvent;
import com.microsoft.embeddedsocial.event.data.SearchTextChangedEvent;
import com.microsoft.embeddedsocial.provider.AbstractEmbeddedSocialSearchSuggestionProvider;
import com.microsoft.embeddedsocial.sdk.Options;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.ui.activity.base.BaseTabsActivity;
import com.microsoft.embeddedsocial.ui.fragment.FeedViewMenuListenerFragment;
import com.microsoft.embeddedsocial.ui.fragment.search.SearchPagerAdapter;
import com.microsoft.embeddedsocial.ui.fragment.search.SearchTextHolder;
import com.microsoft.embeddedsocial.ui.theme.Theme;
import com.squareup.otto.Subscribe;

import java.util.EnumMap;
import java.util.Map;

/**
 * Activity to search topics by hashtags.
 */
public class SearchActivity extends BaseTabsActivity implements SearchView.OnSuggestionListener, SearchTextHolder {
	public static final String NAME = "Search";

	private static final String PREF_SEARCH_TEXT_TOPICS = "topics";
	private static final String PREF_SEARCH_TEXT_PEOPLE = "people";

	private static final String HASHTAG = "#";
	private SearchView searchView;
	private View contentView;
	private SearchHistory searchHistory;

	private Map<SearchType, String> searchText = new EnumMap<>(SearchType.class);

	public SearchActivity() {
		super(R.id.es_navigationSearch);
		setTheme(Theme.SEARCH);
	}

	@Override
	protected PagerAdapter createPagerAdapter() {
		return new SearchPagerAdapter(this, getSupportFragmentManager(), this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			setSearchText(SearchType.TOPICS, savedInstanceState.getString(PREF_SEARCH_TEXT_TOPICS));
			setSearchText(SearchType.PEOPLE, savedInstanceState.getString(PREF_SEARCH_TEXT_PEOPLE));
		}
		this.searchHistory = new SearchHistory();
		contentView = findView(R.id.es_content);
		findView(R.id.es_transparentCover).setOnTouchListener((v, event) -> {
			resetSearchViewFocus();
			return false;
		});
	}

	@Override
	protected void initView(Bundle savedInstanceState) {
		super.initView(savedInstanceState);
		if (customToolbarColorizer != null) {
			setQueryTextColor();
		}
	}

	@Override
	protected int getLayoutResId() {
		return R.layout.es_activity_search;
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		updateSearchTypeInProvider(getCurrentSearchType());
		resetSearchViewFocus();
	}

	private SearchType getCurrentSearchType() {
		return SearchType.values()[getCurrentPagePosition()];
	}

	@Override
	protected void onNewIntent(Intent intent) {
		handleNewIntent(intent);
		notifyTabsChanged();
	}

	@Override
	protected void setupActionBar(ActionBar actionBar) {
		super.setupActionBar(actionBar);
		String existQuery = null;
		if (searchView != null) {
			existQuery = searchView.getQuery().toString();
		}

		searchView = findView(R.id.es_searchView);

		setupSearchView();
		if (!TextUtils.isEmpty(existQuery)) {
			searchView.setQuery(existQuery, false);
		} else if (isStartSearch(getIntent())) {
			handleStartIntent(getIntent());
		}
	}

	@Override
	protected void onPause() {
		EventBus.unregister(this);
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		EventBus.register(this);
	}

	@Override
	public boolean onSuggestionSelect(int position) {
		return false;
	}

	@Override
	public boolean onSuggestionClick(int position) {
		resetSearchViewFocus();
		searchView.setQuery(getSuggestion(position), true);
		return true;
	}

	@Subscribe
	public void onTrendingHashtagSelected(OnTrendingHashtagSelectedEvent event) {
		searchView.setQuery(event.getTrendingHashtag(), true);
	}

	private void setupSearchView() {
		SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
		SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());
		searchView.setSearchableInfo(searchableInfo);
		searchView.setOnSuggestionListener(this);
		searchView.onActionViewExpanded();
	}

	private void handleStartIntent(Intent intent) {
		searchView.setQuery(HASHTAG + intent.getData().getHost(), true);
	}

	private boolean isStartSearch(Intent intent) {
		return Intent.ACTION_VIEW.equals(intent.getAction()) && intent.getData() != null;
	}

	private void handleNewIntent(Intent intent) {
		if (intent != null) {
			Bundle extras = intent.getExtras();
			if (extras != null && extras.containsKey(RecognizerIntent.EXTRA_RESULTS)) {
				// Send voice request as query to fill searchView and skip onSaveInstanceState
				String query = intent.getStringExtra(SearchManager.QUERY);
				searchView.setQuery(getCurrentSearchType() == SearchType.TOPICS ? HASHTAG + query : query, true);
				return;
			}

			if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
				requestData(intent.getStringExtra(SearchManager.QUERY));
			}
		}
	}

	private void requestData(String query) {
		searchHistory.addEntry(query, getCurrentSearchType());
		setSearchText(getCurrentSearchType(), query);
	}

	private String getSuggestion(int position) {
		Cursor cursor = (Cursor) searchView.getSuggestionsAdapter().getItem(position);
		return cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
	}

	private void resetSearchViewFocus() {
		ViewUtils.hideKeyboard(this);
		contentView.requestFocus();
	}

	@Override
	public String getSearchText(SearchType searchType) {
		return searchText.get(searchType);
	}

	@Override
	public boolean isSearchTextEmpty(SearchType searchType) {
		return TextUtils.isEmpty(getSearchText(searchType));
	}

	private void setSearchText(SearchType searchType, String newSearchText) {
		searchText.put(searchType, newSearchText);
		EventBus.post(new SearchTextChangedEvent(searchType, newSearchText));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(PREF_SEARCH_TEXT_TOPICS, getSearchText(SearchType.TOPICS));
		outState.putString(PREF_SEARCH_TEXT_PEOPLE, getSearchText(SearchType.PEOPLE));
	}

	private void updateSearchTypeInProvider(SearchType currentSearchType) {
		String providerAuthority = AbstractEmbeddedSocialSearchSuggestionProvider.getDefaultAuthority(this);
		ContentProviderClient client = getContentResolver().acquireContentProviderClient(providerAuthority);
		AbstractEmbeddedSocialSearchSuggestionProvider provider
			= (AbstractEmbeddedSocialSearchSuggestionProvider) client.getLocalContentProvider();
		provider.setSearchType(currentSearchType);
		client.release();
	}

	private String resolveHintString(SearchType searchType) {
		return getString(searchType == SearchType.TOPICS ? R.string.es_hint_search_topics : R.string.es_hint_search_people);
	}

	@Override
	protected void onPageSelected(int position) {
		invalidateOptionsMenu();
		SearchType searchType = getCurrentSearchType();
		updateSearchTypeInProvider(searchType);
		String newQuery = getSearchText(searchType);
		searchView.setQuery(newQuery, false);
		searchView.setQueryHint(resolveHintString(searchType));
		resetSearchViewFocus();
	}

	private void setQueryTextColor() {
		findChildViews(searchView, ContextCompat.getColor(this, customToolbarColorizer.getTextColor()));
	}

	private void findChildViews(ViewGroup curr, int color) {
		for (int i = 0; i < curr.getChildCount(); i++) {
			View child = curr.getChildAt(i);
			if (child instanceof TextView) {
				// color the text view
				((TextView) child).setTextColor(color);
				if (child instanceof EditText) {
					// color the edit text
					((EditText) child).setHintTextColor(color);
				}
			} else if (child instanceof ImageView) {
				// color the microphone image
				((ImageView)child).setColorFilter(color);
			} else if (child instanceof ViewGroup) {
				// recurse down this view group
				findChildViews((ViewGroup) child, color);
			}
		}
	}

	@Override
	protected void setupFragments() {
		super.setupFragments();
		getSupportFragmentManager().beginTransaction().add(new FeedViewMenuListenerFragment(), FeedViewMenuListenerFragment.TAG).commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (getCurrentSearchType() == SearchType.TOPICS && isTopicsSearchTextNotEmpty()) {
			Options options = GlobalObjectRegistry.getObject(Options.class);
			if (options != null && options.showGalleryView()) {
				getMenuInflater().inflate(R.menu.es_feed_display_method, menu);
			}
		}
		// Call into super method to color hamburger menu
		super.onCreateOptionsMenu(menu);

		return true;
	}

	private boolean isTopicsSearchTextNotEmpty() {
		return !TextUtils.isEmpty(getSearchText(SearchType.TOPICS));
	}

	@Override
	protected String getName() {
		return NAME;
	}
}

