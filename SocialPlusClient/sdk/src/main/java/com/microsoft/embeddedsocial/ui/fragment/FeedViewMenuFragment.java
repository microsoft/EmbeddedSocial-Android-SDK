/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.fragment;

import android.view.Menu;
import android.view.MenuInflater;

/**
 * Adds gallery/list switch to the options menu.
 */
public class FeedViewMenuFragment extends FeedViewMenuListenerFragment {

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		getMenuModule().inflateMenu(menu, inflater);
	}

}
