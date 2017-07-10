/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Encapsulates base data to any operation with content.
 */
public abstract class ContentOperationData implements Parcelable {
	private final String handle;

	public ContentOperationData(String handle) {
		this.handle = handle;
	}

	public ContentOperationData(Parcel in) {
		this.handle = in.readString();
	}

	public String getHandle() {
		return handle;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.handle);
	}
}
