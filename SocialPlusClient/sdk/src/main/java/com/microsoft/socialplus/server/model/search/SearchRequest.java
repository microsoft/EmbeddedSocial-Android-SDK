/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.search;

import com.microsoft.socialplus.server.model.FeedUserRequest;

public class SearchRequest extends FeedUserRequest {

	private final String query;

	public SearchRequest(String query) {
		this.query = query;
	}

}
