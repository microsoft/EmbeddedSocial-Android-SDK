/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.server.model.search;

import com.microsoft.socialplus.autorest.SearchOperations;
import com.microsoft.socialplus.autorest.SearchOperationsImpl;
import com.microsoft.socialplus.server.model.FeedUserRequest;

public class SearchRequest extends FeedUserRequest {

	protected static final SearchOperations SEARCH = new SearchOperationsImpl(RETROFIT, CLIENT);

	protected final String query;

	protected SearchRequest(String query) {
		this.query = query;
	}
}
