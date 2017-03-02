/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.google.common.io.Files;
import com.microsoft.embeddedsocial.server.model.image.AddImageRequest;
import com.microsoft.embeddedsocial.autorest.models.ImageType;
import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

public class ImageUploader {

	public static synchronized String uploadImage(Context context, Uri imageUri, ImageType imageType)
			throws IOException, NetworkRequestException {

		ContentResolver contentResolver = context.getContentResolver();
		String mimeType = contentResolver.getType(imageUri);

		File outputDir = context.getCacheDir();
		MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
		File outputFile = File.createTempFile(
				"img",
				TextUtils.isEmpty(mimeType) ? null : "." + mimeTypeMap.getExtensionFromMimeType(mimeType),
				outputDir);
		try {
			InputStream inputStream = contentResolver.openInputStream(imageUri);
			BufferedSource source = Okio.buffer(Okio.source(inputStream));
			BufferedSink destination = Okio.buffer(Okio.sink(outputFile));
			try {
				destination.writeAll(source);
			} finally {
				source.close();
				destination.close();
			}
			return uploadImageInternal(outputFile, imageType);
		} finally {
			outputFile.delete();
		}
	}

	public static synchronized String uploadImage(File image, ImageType imageType)
			throws IOException, NetworkRequestException {
		if (!image.exists()) {
			throw new FileNotFoundException();
		}
		return uploadImageInternal(image, imageType);
	}

	private static String uploadImageInternal(File image, ImageType imageType)
			throws NetworkRequestException, IOException {

		IImageService imageService
				= GlobalObjectRegistry.getObject(EmbeddedSocialServiceProvider.class).getImageService();
		AddImageRequest addImageRequest = new AddImageRequest(Files.toByteArray(image), imageType);
		return imageService.addImage(addImageRequest);
	}
}
