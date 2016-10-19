/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.sdk;

import android.support.v4.app.Fragment;

/**
 * Factory interface to generate menu drawer fragments for use in Social Plus navigation menu.
 */
public interface INavigationDrawerFactory {

	/**
	 * Creates navigation menu fragment.
	 * @return  {@link Fragment} instance.
	 */
	Fragment createMenuFragment();
}
