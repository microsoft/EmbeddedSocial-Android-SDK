/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.ui.fragment;

import com.microsoft.socialplus.ui.fragment.base.BaseFragment;
import com.microsoft.socialplus.ui.fragment.module.FeedViewMenuModule;

/**
 * This fragment listens to the menu actions for list/gallery switch.
 */
public class FeedViewMenuListenerFragment extends BaseFragment {

	public static final String TAG = "feedViewMenu";
	private final FeedViewMenuModule menuModule = new FeedViewMenuModule(this);

	public FeedViewMenuListenerFragment() {
		addModule(menuModule);
		setHasOptionsMenu(true);
	}

	@Override
	protected int getLayoutId() {
		return 0;
	}

	protected FeedViewMenuModule getMenuModule() {
		return menuModule;
	}
}
