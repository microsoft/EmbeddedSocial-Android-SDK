/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.fetcher.base;

import com.microsoft.embeddedsocial.ui.fragment.base.BaseContentFragment;

/**
 * State of view/fragment/activity displaying the data (see {@link BaseContentFragment}).
 */
public enum ViewState {

	/**
	 * The data is empty.
	 */
	EMPTY,

	/**
	 * No data due to an error.
	 */
	ERROR,

	/**
	 * The first data page is loading.
	 */
	LOADING,

	/**
	 * The data is being refreshing.
	 */
	REFRESHING,

	/**
	 * Data is ready to be displayed (no refresh operation is going on).
	 */
	DATA
}
