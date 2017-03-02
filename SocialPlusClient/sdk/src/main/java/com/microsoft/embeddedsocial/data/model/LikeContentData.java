/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.model;

import android.os.Parcel;

import com.microsoft.embeddedsocial.autorest.models.ContentType;

/**
 * Encapsulates data for like content.
 */
public class LikeContentData extends ContentOperationData {
	private final ContentType contentType;

	public LikeContentData(String contentHandle, ContentType contentType) {
		super(contentHandle);
		this.contentType = contentType;
	}

	public LikeContentData(Parcel in) {
		super(in);
		this.contentType = ContentType.values()[in.readInt()];
	}

	public ContentType getContentType() {
		return contentType;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeInt(this.contentType.ordinal());
	}

	public static final Creator<LikeContentData> CREATOR = new Creator<LikeContentData>() {
		public LikeContentData createFromParcel(Parcel source) {
			return new LikeContentData(source);
		}

		public LikeContentData[] newArray(int size) {
			return new LikeContentData[size];
		}
	};

}
