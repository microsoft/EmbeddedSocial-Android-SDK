/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.base.utils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Utils for convenient reading/writing of some types to parcel
 */
public final class ParcelUtils {

	private ParcelUtils() {
	}

	public static <T extends Parcelable> T readParcelable(Parcel p, Class<T> clazz) {
		return p.readParcelable(clazz.getClassLoader());
	}

	public static <T> T readValue(Parcel p, Class<T> valueType) {
		return valueType.cast(p.readValue(valueType.getClassLoader()));
	}

	public static void writeBoolean(Parcel p, boolean value) {
		p.writeByte((byte) (value ? 1 : 0));
	}

	public static boolean readBoolean(Parcel p) {
		return p.readByte() != 0;
	}
}
