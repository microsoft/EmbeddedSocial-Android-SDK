/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.fragment;

import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.sdk.Options;
import com.microsoft.embeddedsocial.ui.fragment.module.FeedViewMenuModule;
import com.microsoft.embeddedsocial.ui.fragment.base.BaseFragment;

/**
 * This fragment listens to the menu actions for list/gallery switch.
 */
public class FeedViewMenuListenerFragment extends BaseFragment {

	public static final String TAG = "feedViewMenu";
	private final FeedViewMenuModule menuModule = new FeedViewMenuModule(this);

	public FeedViewMenuListenerFragment() {
		Options options = GlobalObjectRegistry.getObject(Options.class);
		if (options != null && options.showGalleryView()) {
			addModule(menuModule);
			setHasOptionsMenu(true);
		}
	}

	@Override
	protected int getLayoutId() {
		return 0;
	}

	protected FeedViewMenuModule getMenuModule() {
		return menuModule;
	}
}
