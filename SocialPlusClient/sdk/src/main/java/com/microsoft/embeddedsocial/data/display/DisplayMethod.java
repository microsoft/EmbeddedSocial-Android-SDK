/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.display;

import android.content.Context;

import com.microsoft.embeddedsocial.sdk.R;


/**
 * Feeds display method
 */
public enum DisplayMethod {
	LIST,
	GALLERY;

	public DisplayMethod next() {
		switch (this) {
			case GALLERY:
				return LIST;
			default:
				return GALLERY;
		}
	}

	/**
	 * Gets the left and right padding for feed item with this display method.
	 */
	public int getHorizontalPadding(Context context) {
		return context.getResources()
			.getDimensionPixelOffset(this == GALLERY ? R.dimen.es_grid_half_padding : R.dimen.es_card_list_padding_hor);
	}

	/**
	 * Gets the top and bottom padding for feed item with this display method.
	 */
	public int getVerticalPadding(Context context) {
		return context.getResources()
			.getDimensionPixelOffset(this == GALLERY ? R.dimen.es_grid_half_padding : R.dimen.es_card_list_padding_ver);
	}

}
