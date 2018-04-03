/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.fetcher.base;

/**
 * Type of request.
 */
public enum RequestType {
	/**
	 * Regular request.
	 */
	REGULAR,

	/**
	 * Should read from the beginning (for pull-to-refresh functionality)
	 */
	FORCE_REFRESH,

	/**
	 * Request to update data from the cache.
	 */
	SYNC_WITH_CACHE;

	public boolean isFullDataReloadRequired() {
		return this != REGULAR;
	}
}
