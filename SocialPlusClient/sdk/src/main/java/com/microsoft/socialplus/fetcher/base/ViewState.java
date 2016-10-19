/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.fetcher.base;

/**
 * State of view/fragment/activity displaying the data (see {@link com.microsoft.socialplus.ui.fragment.base.BaseContentFragment}).
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
