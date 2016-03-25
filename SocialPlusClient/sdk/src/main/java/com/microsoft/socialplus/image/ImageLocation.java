/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.image;

import android.text.TextUtils;

import com.microsoft.socialplus.autorest.models.ImageType;

/**
 * Image url bound to it's size.
 */
public abstract class ImageLocation {
	public static final int TOPIC_SIZE_MAX = 1000;

	private final String originalUrl;

	private ImageLocation(String originalUrl) {
		this.originalUrl = originalUrl;
	}

	public String getOriginalUrl() {
		return originalUrl;
	}

	public abstract String getUrl(int availableWidth);

	public static ImageLocation createTopicImageLocation(String url) {
		return createImageLocation(ImageType.CONTENTBLOB, url);
	}

	public static ImageLocation createUserPhotoImageLocation(String url) {
		return createImageLocation(ImageType.USERPHOTO, url);
	}

	public static ImageLocation createLocalImageLocation(String fileUrl) {
		return TextUtils.isEmpty(fileUrl) ? null : new LocalImageLocationImpl(fileUrl);
	}

	private static ImageLocation createImageLocation(ImageType imageType, String url) {
		if (TextUtils.isEmpty(url)) {
			return null;
		} else {
			return new ServerImageLocationImpl(imageType, url);
		}
	}

	/**
	 * Image size variant.
	 */
	private static class SizeVariant {
		final int width;
		final String suffix;

		SizeVariant(int width, String suffix) {
			this.suffix = suffix;
			this.width = width;
		}
	}

	/**
	 * Image location implementation for a local image without resizing.
	 */
	private static final class LocalImageLocationImpl extends ImageLocation {

		private LocalImageLocationImpl(String originalUrl) {
			super(originalUrl);
		}

		@Override
		public String getUrl(int availableWidth) {
			return getOriginalUrl();
		}
	}

	/**
	 * Image location implementation for a server image.
	 */
	private static final class ServerImageLocationImpl extends ImageLocation {

		private static final SizeVariant[] USER_SIZE_VARIANTS = {
			new SizeVariant(25, "u"),
			new SizeVariant(50, "v"),
			new SizeVariant(100, "w"),
			new SizeVariant(250, "x"),
			new SizeVariant(500, "y"),
			new SizeVariant(1000, "z")
		};

		private static final SizeVariant[] TOPIC_SIZE_VARIANTS = {
			new SizeVariant(25, "t"),
			new SizeVariant(50, "s"),
			new SizeVariant(100, "m"),
			new SizeVariant(250, "l"),
			new SizeVariant(500, "h"),
			new SizeVariant(TOPIC_SIZE_MAX, "g")
		};

		private final SizeVariant[] sizeVariants;

		private ServerImageLocationImpl(ImageType imageType, String originalUrl) {
			super(originalUrl);
			switch (imageType) {
				case CONTENTBLOB:
					sizeVariants = TOPIC_SIZE_VARIANTS;
					break;
				case USERPHOTO:
					sizeVariants = USER_SIZE_VARIANTS;
					break;
				default:
					throw new IllegalArgumentException("Unknown imageType");
			}
		}

		public String getUrl(int availableWidth) {
			SizeVariant largestSize = sizeVariants[sizeVariants.length - 1];
			if (availableWidth >= largestSize.width) {
				return getUrl(largestSize);
			}
			for (int i = sizeVariants.length - 1; i > 0; i--) {
				SizeVariant largerSize = sizeVariants[i];
				SizeVariant smallerSize = sizeVariants[i - 1];
				if (availableWidth >= smallerSize.width) {
					SizeVariant sizeVariant = largerSize.width - availableWidth > availableWidth - smallerSize.width ? smallerSize : largerSize;
					return getUrl(sizeVariant);
				}
			}
			return getUrl(sizeVariants[0]);
		}

		private String getUrl(SizeVariant sizeVariant) {
			return getOriginalUrl() + sizeVariant.suffix;
		}

	}
}
