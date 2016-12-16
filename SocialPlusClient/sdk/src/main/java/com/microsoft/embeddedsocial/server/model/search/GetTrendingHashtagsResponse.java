/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model.search;

import com.microsoft.embeddedsocial.server.model.ListResponse;

import java.util.Collections;
import java.util.List;

public class GetTrendingHashtagsResponse implements ListResponse<String> {

	private List<String> hashtags;

	public GetTrendingHashtagsResponse(List<String> hashtags) {
		this.hashtags = hashtags;
	}

	@Override
	public List<String> getData() {
		return hashtags != null ? hashtags : Collections.emptyList();
	}

	@Override
	public String getContinuationKey() {
		return null;
	}
}
