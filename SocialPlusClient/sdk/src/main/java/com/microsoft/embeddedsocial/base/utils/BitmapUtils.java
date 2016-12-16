/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.base.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Point;
import android.net.Uri;

import com.microsoft.embeddedsocial.image.ImageLoader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Helper for work with bitmaps.
 */
public final class BitmapUtils {

	/**
	 * Determines maximum possible sizes of a resulting bitmap. The original bitmap will be resized to fit them.
	 */
	public interface SizeSpec {

		Point resolveRequiredSize(Point originalSize);

	}

	private BitmapUtils() {
	}

	public static Bitmap loadImage(Context context, Uri imageUri, SizeSpec sizeSpec) throws IOException {
		Point requiredSize = resolveRequiredSize(context, imageUri, sizeSpec);
		return ImageLoader.loadSync(String.valueOf(imageUri), requiredSize);
	}

	private static Point resolveRequiredSize(Context context, Uri imageUri, SizeSpec sizeSpec) throws FileNotFoundException {
		Options options = new Options();
		options.inJustDecodeBounds = true;
		decodeBitmap(context, imageUri, options);
		Point originalSize = new Point(options.outWidth, options.outHeight);
		return sizeSpec.resolveRequiredSize(originalSize);
	}

	private static Bitmap decodeBitmap(Context context, Uri uri, Options options) throws FileNotFoundException {
		InputStream inputStream = StreamUtils.openStream(context, uri);
		try {
			return BitmapFactory.decodeStream(inputStream, null, options);
		} finally {
			StreamUtils.closeSafely(inputStream);
		}
	}

}
