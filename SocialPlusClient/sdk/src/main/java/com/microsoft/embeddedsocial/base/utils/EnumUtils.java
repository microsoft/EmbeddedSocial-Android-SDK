/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.base.utils;

import android.content.Intent;
import android.os.Bundle;

/**
 *
 */
public final class EnumUtils {

	private EnumUtils() { }

	public static <T extends Enum<T>> T valueToEnum(Class<T> enumClass, int value) {
		return enumClass.getEnumConstants()[value];
	}

	public static <T extends Enum<T>> T valueToEnum(Class<T> enumClass, int value, T defaultValue) {
		T[] values = enumClass.getEnumConstants();
		if (value < 0 || value >= values.length) {
			return defaultValue;
		} else {
			return values[value];
		}
	}

	public static void putValue(Bundle bundle, String key, Enum<?> value) {
		int ordinal = value != null ? value.ordinal() : -1;
		bundle.putInt(key, ordinal);
	}

	public static <T extends Enum<T>> T getValue(Bundle bundle, String key, Class<T> enumClass) {
		int ordinal = bundle.getInt(key, -1);
		return valueToEnum(enumClass, ordinal, null);
	}

	public static void putValue(Intent intent, String key, Enum<?> value) {
		int ordinal = value != null ? value.ordinal() : -1;
		intent.putExtra(key, ordinal);
	}

	public static <T extends Enum<T>> T getValue(Intent intent, String key, Class<T> enumClass) {
		int ordinal = intent.getIntExtra(key, -1);
		return valueToEnum(enumClass, ordinal, null);
	}
}
