/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.image;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.microsoft.embeddedsocial.base.LimitedSizeHashMap;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Map;

/**
 * Manages the image loading for ImageView.
 */
public abstract class ImageViewContentLoader {

	private static final Map<String, Boolean> FAILED_DOWNLOADS = new LimitedSizeHashMap<>(100);

	private final ImageView imageView;

	private Target target;

	public ImageViewContentLoader(ImageView imageView) {
		this.imageView = imageView;
	}

	public ImageView getImageView() {
		return imageView;
	}

	protected void onBitmapLoaded(Bitmap bitmap) {
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		imageView.setImageBitmap(bitmap);
	}

	protected abstract void onBitmapFailed();

	public void load(ImageLocation imageLocation, int availableWidth) {
		String uri = imageLocation.getUrl(availableWidth);
		target = new InnerTarget(uri);
		ImageLoader.load(target, uri, availableWidth);
	}

	public void load(String uri) {
		target = new InnerTarget(uri);
		ImageLoader.load(target, uri);
	}

	public void cancel() {
		if (target != null) {
			ImageLoader.cancel(target);
			target = null;
		}
	}

	public void setImageResource(int resourceId) {
		imageView.setImageResource(resourceId);
	}


	/**
	 * Inner target implementation.
	 */
	private final class InnerTarget implements Target {

		private final String uri;

		private InnerTarget(String uri) {
			this.uri = uri;
		}

		@Override
		public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
			FAILED_DOWNLOADS.remove(uri);
			ImageViewContentLoader.this.onBitmapLoaded(bitmap);
		}

		@Override
		public void onBitmapFailed(Drawable errorDrawable) {
			FAILED_DOWNLOADS.put(uri, true);
			ImageViewContentLoader.this.onBitmapFailed();
		}

		@Override
		public void onPrepareLoad(Drawable placeHolderDrawable) {
			if (isFailed()) {
				ImageViewContentLoader.this.onBitmapFailed();
			} else {
				imageView.setImageDrawable(placeHolderDrawable);
			}
		}

		private boolean isFailed() {
			return FAILED_DOWNLOADS.containsKey(uri) ? FAILED_DOWNLOADS.get(uri) : false;
		}
	}
}
