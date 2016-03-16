/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.search;

import com.microsoft.socialplus.server.model.ListResponse;

import java.util.List;

public class AutocompletedHashtagsResponse implements ListResponse<String> {

	private List<String> suggestions;

	public AutocompletedHashtagsResponse(List<String> suggestions) {
		this.suggestions = suggestions;
	}

	@Override
	public List<String> getData() {
		return suggestions;
	}

	@Override
	public String getContinuationKey() {
		return null;
	}
}
