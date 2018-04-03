/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model;

import java.util.List;

/**
 * Base class for server responses returning a list of items.
 *
 * @param <T> item type
 */
public abstract class ListResponse<T> {

	private String continuationKey;

	public String getContinuationKey() {
		return continuationKey;
	}

	public void setContinuationKey(String continuationKey) {
		this.continuationKey = continuationKey;
	}

	public abstract List<T> getData();
}
