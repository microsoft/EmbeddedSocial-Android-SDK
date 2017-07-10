/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.adapter.viewholder;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Implementation of {@link ViewHolder} for a single view layout.
 */
public class SingleViewHolder extends BaseViewHolder {

	public SingleViewHolder(View itemView) {
		super(itemView);
	}

	public static SingleViewHolder create(@LayoutRes int layoutId, ViewGroup parent) {
		View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
		return new SingleViewHolder(view);
	}

}
