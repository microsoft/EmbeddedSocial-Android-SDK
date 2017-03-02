/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.adapter.renderer;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.ViewGroup;

/**
 * Renders items of some type.
 *
 * @param <T> item type
 * @param <V> view holder
 */
public abstract class Renderer<T, V extends ViewHolder> {

	public abstract V createViewHolder(ViewGroup parent);

	protected void onItemRendered(T item, V holder) {
	}

	public final void renderItem(T topic, ViewHolder holder) {
		//noinspection unchecked
		onItemRendered(topic, (V) holder);
	}

}
