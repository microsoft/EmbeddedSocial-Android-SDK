/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.util;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Adds padding for the top and bottom items in RecyclerView.
 */
public class VerticalPaddingDecoration extends RecyclerView.ItemDecoration {
	private final int topPadding;
	private final int bottomPadding;

	public VerticalPaddingDecoration(int verticalPadding) {
		this(verticalPadding, verticalPadding);
	}

	public VerticalPaddingDecoration(int bottomPadding, int topPadding) {
		this.bottomPadding = bottomPadding;
		this.topPadding = topPadding;
	}

	@Override
	public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
		int position = parent.getChildAdapterPosition(view);
		if (position == 0) {
			outRect.top = topPadding;
		} else {
			int itemCount = parent.getAdapter().getItemCount();
			if (position == itemCount - 1) {
				outRect.bottom = bottomPadding;
			}
		}
	}
}
