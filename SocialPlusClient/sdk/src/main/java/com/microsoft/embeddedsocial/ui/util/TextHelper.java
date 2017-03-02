/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.util;

import android.text.TextUtils;

/**
 * Helper class to work with strings.
 */
public final class TextHelper {
	private TextHelper() { /* cannot be instantiated */ }

	/**
	 * Returns true if the string is null or 0-length when spaces and control
	 * characters were trimmed from the start and end.
	 * @param str the string to be examined
	 * @return true if str is null or zero length
	 */
	public static boolean isEmpty(CharSequence str) {
		return str == null || TextUtils.getTrimmedLength(str) == 0;
	}

	public static boolean areEqual(String s1, String s2) {
		if (isEmpty(s1)) {
			return isEmpty(s2);
		} else {
			return s1.trim().equals(s2.trim());
		}
	}
}
