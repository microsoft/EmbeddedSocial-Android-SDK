/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.model;

import android.os.Parcel;

/**
 * Encapsulates data to remove any kind of content.
 */
public class RemoveContentData extends ContentOperationData {

	public RemoveContentData(String handle) {
		super(handle);
	}

	public RemoveContentData(Parcel in) {
		super(in);
	}

	public static final Creator<RemoveContentData> CREATOR = new Creator<RemoveContentData>() {
		public RemoveContentData createFromParcel(Parcel source) {
			return new RemoveContentData(source);
		}

		public RemoveContentData[] newArray(int size) {
			return new RemoveContentData[size];
		}
	};
}
