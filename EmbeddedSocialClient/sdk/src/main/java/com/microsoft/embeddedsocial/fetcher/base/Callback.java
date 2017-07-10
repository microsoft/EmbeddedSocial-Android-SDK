/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.fetcher.base;

/**
 * Callback for data loading results.
 */
public class Callback {

	/**
	 * Called after successful completion of data request.
	 */
	public void onDataRequestSucceeded() {
	}

	/**
	 * Called after failure of data request.
	 */
	public void onDataRequestFailed(Exception e) {
	}

	/**
	 * Called after data removing.
	 */
	public void onDataRemoved() {
	}

	/**
	 * Called after data update (not caused by a data request)
	 */
	public void onDataUpdated() {
	}

	/**
	 * Fetcher's state changed.
	 *
	 * @param newState new fetcher's state
	 */
	public void onStateChanged(FetcherState newState) {
	}
}
