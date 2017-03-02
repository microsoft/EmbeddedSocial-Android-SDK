/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Stores images for pending posts.
 */
public class ImageStorage {

	private static final String IMAGES_FOLDER_PATH = "stored_images";

	private final Context context;

	/**
	 * Creates an instance.
	 * @param context	valid context
	 */
	public ImageStorage(Context context) {
		this.context = context;
	}

	private File createNewImageFile(String extension) throws IOException {
		if (TextUtils.isEmpty(extension)) {
			return new File(getStorageDir(), UUID.randomUUID().toString());
		} else {
			return new File(getStorageDir(), UUID.randomUUID().toString() + "." + extension);
		}
	}

	private static void copyStream(InputStream source, OutputStream dest) throws IOException {
		byte[] buffer = new byte[16384];
		int length;

		while ((length = source.read(buffer)) > 0) {
			dest.write(buffer, 0, length);
		}
	}

	private File getStorageDir() throws IOException {
		File storageDir = new File(context.getFilesDir(), IMAGES_FOLDER_PATH);
		if (!storageDir.exists() && !storageDir.mkdir()) {
			throw new IOException("Failed to create storage dir: " + storageDir.getAbsolutePath());
		}
		return storageDir;
	}

	/**
	 * Stores an image.
	 * @param 	uri		image URI
	 * @return	{@linkplain StoredImage} instance.
	 * @throws IOException
	 */
	@SuppressLint("NewApi")
	public StoredImage storeImage(Uri uri) throws IOException {
		ContentResolver contentResolver = context.getContentResolver();
		String mimeType = contentResolver.getType(uri);
		String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
		File destImage = createNewImageFile(extension);

		try (InputStream sourceImage = contentResolver.openInputStream(uri);
			OutputStream dest = new FileOutputStream(destImage)) {
			copyStream(sourceImage, dest);
		}

		return new StoredImage(destImage);
	}

	/**
	 * An image stored in the local image storage.
	 */
	public class StoredImage {

		private final File imageFile;

		private StoredImage(File imageFile) {
			this.imageFile = imageFile;
		}

		/**
		 * Gets path to the image.
		 * @return	path to the image.
		 */
		public String getImagePath() {
			return imageFile.getAbsolutePath();
		}

		/**
		 * Gets a Uri to the image.
		 * @return  image Uri.
		 */
		public Uri getImageUri() {
			return Uri.fromFile(imageFile);
		}
	}
}
