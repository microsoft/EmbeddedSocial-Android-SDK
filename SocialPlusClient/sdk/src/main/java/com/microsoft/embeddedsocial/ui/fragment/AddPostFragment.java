/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.fragment;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.microsoft.embeddedsocial.base.utils.BitmapUtils;
import com.microsoft.embeddedsocial.base.utils.ObjectUtils;
import com.microsoft.embeddedsocial.data.storage.PostStorage;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.service.IntentExtras;
import com.microsoft.embeddedsocial.service.ServiceAction;
import com.microsoft.embeddedsocial.service.WorkerService;
import com.microsoft.embeddedsocial.ui.fragment.base.BaseEditPostFragment;
import com.microsoft.embeddedsocial.ui.fragment.module.PhotoProviderModule;
import com.microsoft.embeddedsocial.ui.util.FitWidthSizeSpec;
import com.microsoft.embeddedsocial.ui.util.TextHelper;
import com.microsoft.embeddedsocial.autorest.models.PublisherType;

/**
 * Fragment for adding a new post.
 */
public class AddPostFragment extends BaseEditPostFragment implements OnClickListener, PhotoProviderModule.Consumer {
	private static final String PREF_IMAGE_URI = "imageUri";

	private PhotoProviderModule photoProvider;
	private Uri imageUri;

	private PostStorage postStorage;

	/**
	 * Create a new instance of AddPostFragment
	 */
	public static AddPostFragment newInstance() {
		return new AddPostFragment();
	}

	public AddPostFragment() {
		photoProvider = new PhotoProviderModule(this, this);
		addModule(photoProvider);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setHasOptionsMenu(true);

		postStorage = new PostStorage(view.getContext());

		setOnClickListener(view, R.id.es_addPhotoButton, this);
		getImageMessageView().setText(R.string.es_add_picture_suggestion);

		if (savedInstanceState != null) {
			imageUri = savedInstanceState.getParcelable(PREF_IMAGE_URI);
			if (imageUri != null) {
				photoProvider.loadBitmap(imageUri);
			}
		} else {
			loadDataFromExtras();
		}
	}

	private void loadDataFromExtras() {
		Bundle extras = getActivity().getIntent().getExtras();
		if (extras == null) {
			// no extras were passed to the activity
			return;
		}
		String title = extras.getString(IntentExtras.POST_TITLE, "");
		String description = extras.getString(IntentExtras.POST_DESCRIPTION, "");
		String imageUriString = extras.getString(IntentExtras.POST_IMAGE_URI, "");
		boolean automatic = extras.getBoolean(IntentExtras.AUTOMATIC, false);

		if (!TextUtils.isEmpty(title)) {
			getTitleView().setText(title);
		}
		if (!TextUtils.isEmpty(description)) {
			getDescriptionView().setText(description);
		}
		if (!TextUtils.isEmpty(imageUriString)) {
			imageUri = Uri.parse(imageUriString);
			photoProvider.loadBitmap(imageUri);
		}

		if (automatic) {
			onFinishedEditing();
		}
	}

	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.es_addPhotoButton) {
			photoProvider.showSelectImageDialog();
		}
	}

	@Override
	public void onPhotoSelected(Uri newImageUri) {
		this.imageUri = newImageUri;
	}

	@Override
	public void onPhotoLoaded(Uri loadedImageUri, Bitmap thumbnail) {
		if (ObjectUtils.equal(imageUri, loadedImageUri)) {
			ImageView coverView = getCoverView();
			if (thumbnail != null) {
				hideView(R.id.es_imageMessage);
				ViewGroup.LayoutParams layoutParams = coverView.getLayoutParams();
				double imageRatio = (double) thumbnail.getHeight() / thumbnail.getWidth();
				int imageViewWidth = coverView.getWidth();
				int imageViewHeight = (int) (imageViewWidth * imageRatio);
				layoutParams.width = imageViewWidth;
				layoutParams.height = imageViewHeight;
				coverView.setLayoutParams(layoutParams);
			} else {
				showView(R.id.es_imageMessage);
			}
			coverView.setImageBitmap(thumbnail);
		}
	}

	@Override
	public BitmapUtils.SizeSpec getSizeSpec() {
		return new FitWidthSizeSpec(getActivity());
	}

	@Override
	protected void onFinishedEditing() {
		postStorage.storePost(getTitle(), getDescription(), imageUri, PublisherType.USER);
		WorkerService.getLauncher(getContext()).launchService(ServiceAction.SYNC_DATA);
		finishActivity();
	}

	@Override
	protected boolean isInputEmpty() {
		return imageUri == null
			&& TextHelper.isEmpty(getTitle())
			&& TextHelper.isEmpty(getDescription());
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(PREF_IMAGE_URI, imageUri);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		photoProvider.dispose();
	}
}
