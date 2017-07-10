/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.microsoft.embeddedsocial.image.ImageLocation;
import com.microsoft.embeddedsocial.image.ImageViewContentLoader;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.service.IntentExtras;
import com.microsoft.embeddedsocial.ui.fragment.base.BaseFragment;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Settings fragment.
 */
public class ViewImageFragment extends BaseFragment {

	private PhotoViewAttacher photoViewAttacher;
	private FullImageViewContentLoader coverContentLoader;
	private ImageView coverImage;
	private ProgressBar progressBar;

	@Override
	protected int getLayoutId() {
		return R.layout.es_fragment_view_image;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		Bundle arguments = getArguments();
		String coverImageUrl = arguments.getString(IntentExtras.COVER_IMAGE_URL_EXTRA);

		progressBar = (ProgressBar) view.findViewById(R.id.es_progress);
		coverImage = (ImageView) view.findViewById(R.id.es_coverImage);
		coverContentLoader = new FullImageViewContentLoader(coverImage);
		if (savedInstanceState == null) {
			coverImage.setVisibility(View.GONE);
			progressBar.setVisibility(View.VISIBLE);
		}

		ImageLocation imageLocation = ImageLocation.createTopicImageLocation(coverImageUrl);
		coverContentLoader.load(imageLocation, ImageLocation.TOPIC_SIZE_MAX);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (photoViewAttacher != null) {
			photoViewAttacher.cleanup();
		}
	}

	private class FullImageViewContentLoader extends ImageViewContentLoader {

		public FullImageViewContentLoader(ImageView imageView) {
			super(imageView);
		}

		@Override
		protected void onBitmapLoaded(Bitmap bitmap) {
			super.onBitmapLoaded(bitmap);
			progressBar.setVisibility(View.GONE);
			coverImage.setVisibility(View.VISIBLE);
			photoViewAttacher = new PhotoViewAttacher(coverImage);
		}

		@Override
		protected void onBitmapFailed() {
			progressBar.setVisibility(View.GONE);
		}
	}
}
