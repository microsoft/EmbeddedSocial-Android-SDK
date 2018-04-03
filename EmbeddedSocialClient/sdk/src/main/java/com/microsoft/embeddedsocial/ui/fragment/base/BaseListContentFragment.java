/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.fragment.base;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.microsoft.embeddedsocial.fetcher.base.FetchableAdapter;

/**
 * {@link BaseContentFragment} with a {@link LinearLayoutManager} set as layout manager.
 *
 * @param <AT> adapter type
 */
public abstract class BaseListContentFragment<AT extends FetchableAdapter<?, ?>> extends BaseContentFragment<AT> {

	@Override
	protected RecyclerView.LayoutManager createInitialContentLayoutManager() {
		return new LinearLayoutManager(getContext());
	}

}
