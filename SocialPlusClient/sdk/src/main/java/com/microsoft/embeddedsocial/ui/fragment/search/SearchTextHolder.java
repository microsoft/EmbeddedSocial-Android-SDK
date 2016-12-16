/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.fragment.search;

import com.microsoft.embeddedsocial.data.model.SearchType;

/**
 * Object having an access to the search text.
 */
public interface SearchTextHolder {

	String getSearchText(SearchType searchType);

	boolean isSearchTextEmpty(SearchType searchType);

}
