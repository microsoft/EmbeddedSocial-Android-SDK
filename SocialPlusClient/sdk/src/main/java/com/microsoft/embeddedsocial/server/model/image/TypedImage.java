/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model.image;

import android.annotation.SuppressLint;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
//import retrofit.mime.TypedOutput;

public class TypedImage /*implements TypedOutput*/ {

	private final File image;

	public TypedImage(File image) {
		this.image = image;
	}

	public String fileName() {
		return image.getName();
	}

	public String mimeType() {
		return detectMimeType();
	}

	public long length() {
		return -1;
	}

	@SuppressLint("NewApi")
	public void writeTo(OutputStream out) throws IOException {
		BufferedSink bufferedSink = Okio.buffer(Okio.sink(out));
		try (BufferedSource bufferedSource = Okio.buffer(Okio.source(image))) {
			bufferedSink.writeAll(bufferedSource);
			bufferedSink.flush();
		}
	}

	private String detectMimeType() {
		String mimeType = "image/*";
		String fileName = image.getName();
		int periodIndex = fileName.lastIndexOf(".");
		if (periodIndex != -1) {
			String fileExtension = fileName.substring(periodIndex + 1);
			String resolvedMimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);
			if (resolvedMimeType != null) {
				mimeType = resolvedMimeType;
			}
		}
		return mimeType;
	}
}
