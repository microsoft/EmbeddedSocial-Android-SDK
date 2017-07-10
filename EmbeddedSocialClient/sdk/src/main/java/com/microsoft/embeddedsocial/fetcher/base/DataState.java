/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.fetcher.base;

import android.text.TextUtils;

import com.microsoft.embeddedsocial.base.utils.Values;

/**
 * State of a data requests sequence.
 */
public class DataState extends Values {

	private static final String DATA_ENDED = "dataEnded";
	private static final String CURSOR = "continuationKey";

	/**
	 * Marks that there is no more data.
	 */
	public void markDataEnded() {
		setValue(DATA_ENDED, true);
	}

	/**
	 * Whether the data was marked as ended.
	 */
	public boolean isDataEnded() {
		return getBooleanValue(DATA_ENDED);
	}

	/**
	 * Stores the continuation key returned by the server during the last data request.
	 */
	public void setContinuationKey(String continuationKey) {
		setValue(CURSOR, continuationKey);
		if (TextUtils.isEmpty(continuationKey)) {
			markDataEnded();
		}
	}

	/**
	 * Returns the continuation key returned by the server during the last data request (<code>null</code> if there was no requests).
	 */
	public String getContinuationKey() {
		return getValue(CURSOR);
	}

}
