/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.base.utils;

/**
 * Various convenient methods for objects.
 */
public final class ObjectUtils {

	private ObjectUtils() {
	}

	public static boolean equal(Object first, Object second) {
		if (first == null) {
			return second == null;
		} else {
			return first.equals(second);
		}
	}

	public static boolean isOneOf(Object value, Object... valuesOfInterest) {
		for (Object valueOfInterest : valuesOfInterest) {
			if (equal(value, valueOfInterest)) {
				return true;
			}
		}
		return false;
	}
}
