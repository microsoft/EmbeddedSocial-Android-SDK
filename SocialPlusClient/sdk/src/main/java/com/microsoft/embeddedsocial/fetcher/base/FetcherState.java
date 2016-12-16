/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.fetcher.base;

/**
 * State of fetcher.
 */
public enum FetcherState {
	HAS_MORE_DATA,
	LAST_ATTEMPT_FAILED,
	LOADING,
	DATA_ENDED
}
