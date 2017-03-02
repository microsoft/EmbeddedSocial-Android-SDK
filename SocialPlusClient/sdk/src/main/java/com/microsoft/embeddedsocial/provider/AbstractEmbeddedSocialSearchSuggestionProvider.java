/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.provider;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.text.TextUtils;

import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.data.model.SearchType;
import com.microsoft.embeddedsocial.data.storage.SearchHistory;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.server.ISearchService;
import com.microsoft.embeddedsocial.server.EmbeddedSocialServiceProvider;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.search.GetAutocompletedHashtagsRequest;
import com.microsoft.embeddedsocial.server.model.search.AutocompletedHashtagsResponse;

import java.util.List;

/**
 * Abstract search provider to load suggestions to search request.
 */
public abstract class AbstractEmbeddedSocialSearchSuggestionProvider extends ContentProvider {

	private static final String AUTHORITY_POSTFIX = ".embeddedsocial_searchprovider";

	private static final int SUGGESTION_QUERY_MIN_LENGTH = 3;

	private final String[] cursorColumns = {"_id", SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_ICON_1};

	private SearchType searchType = SearchType.TOPICS;

	@Override
	public boolean onCreate() {
		return true;
	}

	/**
	 * Gets default authority of this provider.
	 * @param   context   valid context
	 * @return  provider authority string.
	 */
	public static String getDefaultAuthority(Context context) {
		return context.getPackageName() + AUTHORITY_POSTFIX;
	}

	public void setSearchType(SearchType searchType) {
		this.searchType = searchType;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		if (uri == null || selectionArgs == null) {
			DebugLog.w("Null request");
			return null;
		}

		final String suggestionRequest = selectionArgs[0];

		if (searchType == SearchType.PEOPLE
			|| selectionArgs.length == 0
			|| TextUtils.isEmpty(suggestionRequest)
			|| suggestionRequest.length() < SUGGESTION_QUERY_MIN_LENGTH) {

			return getHistorySearchSuggestion();
		}

		return getServerSearchTopicSuggestion(suggestionRequest);

	}

	private Cursor getHistorySearchSuggestion() {
		MatrixCursor cursor = new MatrixCursor(cursorColumns);
		List<String> history = new SearchHistory().getSearchRequests(searchType);
		for (int i = 0; i < history.size(); i++) {
			cursor.newRow()
				.add(history.get(i).hashCode())
				.add(history.get(i))
				.add(R.drawable.es_ic_clock);
		}
		return cursor;

	}

	private Cursor getServerSearchTopicSuggestion(String suggestionRequest) {
		ISearchService searchService = GlobalObjectRegistry
			.getObject(EmbeddedSocialServiceProvider.class)
			.getSearchService();
		MatrixCursor cursor = null;
		try {
			AutocompletedHashtagsResponse response =
					searchService.searchHashtagsAutocomplete(new GetAutocompletedHashtagsRequest(suggestionRequest));
			cursor = new MatrixCursor(cursorColumns);
			List<String> suggestions = response.getData();
			for (int i = 0; i < suggestions.size(); i++) {
				cursor.newRow()
					.add(suggestions.get(i).hashCode())
					.add(suggestions.get(i))
					.add(R.drawable.es_empty);
			}
		} catch (NetworkRequestException e) {
			DebugLog.logException(e);
		}

		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		return "";
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		return 0;
	}
}
