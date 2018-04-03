/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.fetcher.base;

import java.util.List;

/**
 * Wraps the successfully loaded data and the subsequent exception for cases when data is loaded only partially.
 */
public class PartiallyLoadedDataException extends Exception {

	private final List<?> loadedData;
	private final Exception cause;

	public PartiallyLoadedDataException(List<?> loadedData, Exception cause) {
		this.loadedData = loadedData;
		this.cause = cause;
	}

	public <T> List<T> getLoadedData() {
		//noinspection unchecked
		return (List<T>) loadedData;
	}

	@Override
	public Exception getCause() {
		return cause;
	}
}
