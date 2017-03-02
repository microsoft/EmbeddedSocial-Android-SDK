/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.adapter.viewholder;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Base view holder.
 */
public class BaseViewHolder extends RecyclerView.ViewHolder {
	public BaseViewHolder(View itemView) {
		super(itemView);
	}

	public Context getContext() {
		return itemView.getContext();
	}

	public Resources getResources() {
		return itemView.getResources();
	}
}
