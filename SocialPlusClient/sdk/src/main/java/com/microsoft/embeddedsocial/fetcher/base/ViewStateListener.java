/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.fetcher.base;

/**
 * Listener for {@link ViewState} changes.
 */
public interface ViewStateListener {

	void onViewStateChanged(ViewState viewState, Exception exception);

}
