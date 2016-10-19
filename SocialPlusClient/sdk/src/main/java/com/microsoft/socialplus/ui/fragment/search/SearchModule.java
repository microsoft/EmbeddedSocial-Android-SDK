/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.ui.fragment.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.microsoft.socialplus.base.event.EventBus;
import com.microsoft.socialplus.base.utils.ObjectUtils;
import com.microsoft.socialplus.data.model.SearchType;
import com.microsoft.socialplus.event.data.SearchTextChangedEvent;
import com.microsoft.socialplus.sdk.R;
import com.microsoft.socialplus.ui.activity.SearchActivity;
import com.microsoft.socialplus.ui.fragment.base.BaseContentFragment;
import com.microsoft.socialplus.ui.fragment.base.Module;
import com.squareup.otto.Subscribe;

/**
 * Encapsulates the behavior of search fragments.
 */
class SearchModule extends Module {

	private final BaseContentFragment<?> owner;
	private final SearchType searchType;

	private String query;

	SearchModule(BaseContentFragment<?> owner, SearchType searchType) {
		super(owner);
		this.owner = owner;
		this.searchType = searchType;
	}

	@Override
	protected void onPause() {
		EventBus.unregister(this);
		super.onPause();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		query = getActualQuery();
	}

	@Override
	protected void onResume() {
		super.onResume();
		EventBus.register(this);
		String newQuery = getActualQuery();
		if (!ObjectUtils.equal(query, newQuery)) {
			setQuery(newQuery);
		}
	}

	@Override
	protected void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		owner.setErrorMessage(R.string.sp_message_failed_to_search_data);
		updateEmptyDataMessage();
	}

	private void updateEmptyDataMessage() {
		owner.setEmptyDataMessage(owner.getString(R.string.sp_message_no_search_data_pattern, query));
	}

	private String getActualQuery() {
		SearchActivity ownerActivity = (SearchActivity) owner.getActivity();
		return ownerActivity.getSearchText(searchType);
	}

	@Subscribe
	public void onSearchTextChanged(SearchTextChangedEvent event) {
		if (event.getSearchType() == searchType) {
			setQuery(event.getText());
		}
	}

	private void setQuery(String query) {
		this.query = query;
		owner.resetAdapter();
		updateEmptyDataMessage();
	}

	String getQuery() {
		return query;
	}
}
