/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.model;

import android.os.Parcel;

/**
 * Encapsulates data for pin topic.
 */
public class PinData extends ContentOperationData {
	public PinData(String handle) {
		super(handle);
	}

	public PinData(Parcel in) {
		super(in);
	}

	public static final Creator<PinData> CREATOR = new Creator<PinData>() {
		public PinData createFromParcel(Parcel source) {
			return new PinData(source);
		}

		public PinData[] newArray(int size) {
			return new PinData[size];
		}
	};
}
