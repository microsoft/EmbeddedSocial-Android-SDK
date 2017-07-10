/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.theme;

import android.content.Context;
import android.content.res.TypedArray;

import com.microsoft.embeddedsocial.sdk.R;

/**
 * Helper class for getting attributes from style.
 */
public final class ThemeAttributes {

	private ThemeAttributes() {
	}

	public static int getColor(Context context, int attrId) {
		return getValue(context, typedArray -> typedArray.getColor(attrId, 0));
	}

	public static int getResourceId(Context context, int attrId) {
		return getValue(context, typedArray -> typedArray.getResourceId(attrId, 0));
	}

	private static <T> T getValue(Context context, ValueGetter<T> valueGetter) {
		TypedArray typedArray = context.obtainStyledAttributes(R.styleable.es_AppTheme);
		T result = valueGetter.get(typedArray);
		typedArray.recycle();
		return result;
	}


	/**
	 * Method to obtain value.
	 *
	 * @param <T> type of result
	 */
	private interface ValueGetter<T> {

		T get(TypedArray typedArray);

	}
}
