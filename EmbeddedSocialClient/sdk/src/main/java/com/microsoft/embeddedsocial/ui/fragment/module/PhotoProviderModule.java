/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.fragment.module;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

import com.microsoft.embeddedsocial.base.IDisposable;
import com.microsoft.embeddedsocial.base.utils.BitmapUtils;
import com.microsoft.embeddedsocial.base.utils.ViewUtils;
import com.microsoft.embeddedsocial.data.storage.ImageStorage;
import com.microsoft.embeddedsocial.event.dialog.OnDialogItemSelectedEvent;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.ui.dialog.AlertDialogFragment;
import com.microsoft.embeddedsocial.ui.fragment.base.Module;
import com.microsoft.embeddedsocial.base.event.EventBus;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.event.PermissionRequestResultEvent;
import com.microsoft.embeddedsocial.ui.fragment.base.BaseFragment;
import com.squareup.otto.Subscribe;

import java.io.File;

/**
 * Helps to select images.
 */
public class PhotoProviderModule extends Module implements IDisposable {

	/**
	 * Photo consumer.
	 */
	public interface Consumer {

		void onPhotoSelected(Uri photoUri);

		void onPhotoLoaded(Uri photoUri, Bitmap thumbnail);

		BitmapUtils.SizeSpec getSizeSpec();
	}

	private static final String DIALOG_OPTIONS_BASE = "photoSource";

	private static final int REQUESTCODE_TAKE_PHOTO = 1;
	private static final int REQUESTCODE_CHOOSE_PHOTO = 2;

	private static final int REQUESTCODE_TAKE_PHOTO_PERMISSION = 11;
	private static final int REQUESTCODE_CHOOSE_PHOTO_PERMISSION = REQUESTCODE_TAKE_PHOTO_PERMISSION + 1;

	private static final String PREF_URI = PhotoProviderModule.class.getName() + ".uri";

	private final Consumer consumer;
	private final String dialogId;

	private Uri imageUri;

	private Uri postponedImageUri;
	private Bitmap postponedBitmap;

	private final Object eventListener = new Object() {
		@Subscribe
		public void onPermissionRequestResult(PermissionRequestResultEvent event) {
			if (event.getRequestCode() == REQUESTCODE_TAKE_PHOTO_PERMISSION
				|| event.getRequestCode() == REQUESTCODE_CHOOSE_PHOTO_PERMISSION) {

				if (!event.isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
					DebugLog.e("user denied permission for external storage");
					Toast.makeText(getContext(), R.string.es_message_no_permission, Toast.LENGTH_SHORT).show();
				} else {
					retryActionOnPermissionResult(event);
				}
			}
		}
	};

	private void retryActionOnPermissionResult(PermissionRequestResultEvent event) {
		if (event.getRequestCode() == REQUESTCODE_TAKE_PHOTO_PERMISSION) {
			takeNewPhoto();
		} else if (event.getRequestCode() == REQUESTCODE_CHOOSE_PHOTO_PERMISSION) {
			choosePhoto();
		}
	}

	public PhotoProviderModule(BaseFragment owner, Consumer consumer) {
		super(owner);
		this.consumer = consumer;
		this.dialogId = DIALOG_OPTIONS_BASE + Integer.toString(this.hashCode());
		EventBus.register(eventListener);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			if (resultCode == Activity.RESULT_OK) {
				switch (requestCode) {
					case REQUESTCODE_CHOOSE_PHOTO:
						handleChoosePhotoResult(data);
						break;
					case REQUESTCODE_TAKE_PHOTO:
						handleNewPhotoResult();
						break;
				}
			}
		} catch (Exception e) {
			DebugLog.logException(e);
			showErrorMessage();
		}
	}

	private void showErrorMessage() {
		Toast.makeText(getContext(), R.string.es_message_cant_complete_action, Toast.LENGTH_SHORT).show();
	}

	public void takeNewPhoto() {
		Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (!verifyStoragePermissionIsGranted(REQUESTCODE_TAKE_PHOTO_PERMISSION)) {
			return;
		}
		try {
			imageUri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
		} catch (Exception e) {
			DebugLog.logException(e);
		}
		if (imageUri != null) {
			takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			startActivityForResultSafely(takePhotoIntent, REQUESTCODE_TAKE_PHOTO);
		} else {
			showErrorMessage();
		}
	}

	private boolean verifyStoragePermissionIsGranted(int requestCode) {
		boolean result;

		String externalStoragePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
		int permissionRequestResult = ContextCompat.checkSelfPermission(
			getContext(), externalStoragePermission);
		if (permissionRequestResult != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(
				getOwner().getActivity(),
				new String[]{externalStoragePermission},
				requestCode
			);
			result = false;
		} else {
			result = true;
		}

		return result;
	}

	private void handleNewPhotoResult() {
		consumer.onPhotoSelected(imageUri);
		loadBitmap(imageUri);
	}

	public void choosePhoto() {
		if (!verifyStoragePermissionIsGranted(REQUESTCODE_CHOOSE_PHOTO_PERMISSION)) {
			return;
		}
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		startActivityForResultSafely(intent, REQUESTCODE_CHOOSE_PHOTO);
	}

	private void handleChoosePhotoResult(Intent data) {
		Uri uri = data.getData();
		consumer.onPhotoSelected(uri);
		loadBitmap(uri);
	}

	public void loadBitmap(Uri uri) {
		new ImageLoader().execute(uri);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelable(PREF_URI, imageUri);
	}

	@Override
	protected void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			restoreState(savedInstanceState);
		}
	}

	private void restoreState(@NonNull Bundle bundle) {
		imageUri = bundle.getParcelable(PREF_URI);
	}

	private boolean startActivityForResultSafely(Intent intent, int requestCode) {
		try {
			getOwner().startActivityForResult(intent, requestCode);
			return true;
		} catch (ActivityNotFoundException e) {
			DebugLog.logException(e);
			showErrorMessage();
			return false;
		}
	}

	public void showEditImageDialog() {
		showImageDialog(R.string.es_menu_new_photo, R.string.es_menu_choose_photo, R.string.es_menu_remove_photo);
	}

	public void showSelectImageDialog() {
		showImageDialog(R.string.es_menu_new_photo, R.string.es_menu_choose_photo);
	}

	private void showImageDialog(int... options) {
		ViewUtils.hideKeyboard(getOwner());
		new AlertDialogFragment.Builder(getContext(), dialogId)
			.setItems(options)
			.show(getOwner().getActivity(), null);
	}

	@Subscribe
	public void onDialogItemSelected(OnDialogItemSelectedEvent event) {
		if (dialogId.equals(event.getDialogId())) {
			if (event.getTextId() == R.string.es_menu_new_photo) {
				takeNewPhoto();
			} else if (event.getTextId() == R.string.es_menu_choose_photo) {
				choosePhoto();
			} else if (event.getTextId() == R.string.es_menu_remove_photo) {
				consumer.onPhotoSelected(null);
			}
		}
	}

	@Override
	protected void onPause() {
		EventBus.unregister(this);
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		EventBus.register(this);
		if (postponedBitmap != null) {
			consumer.onPhotoLoaded(postponedImageUri, postponedBitmap);
			postponedBitmap = null;
			postponedImageUri = null;
		}
	}

	@Override
	public void dispose() {
		EventBus.unregister(eventListener);
	}

	/**
	 * Loads a thumbnail for the photo.
	 */
	private class ImageLoader extends AsyncTask<Uri, Void, Bitmap> {

		private ImageStorage imageStorage = new ImageStorage(getContext());

		private Uri originalUri;

		@Override
		protected Bitmap doInBackground(Uri... params) {
			try {
				originalUri = params[0];
				Context context = getContext();
				Bitmap bmp = null;
				// TODO: use this copied image for upload
				ImageStorage.StoredImage localStoredImage = null;
				try {
					localStoredImage = imageStorage.storeImage(originalUri);
					bmp = BitmapUtils.loadImage(context, localStoredImage.getImageUri(), consumer.getSizeSpec());
				} catch (Exception e) {
					DebugLog.logException(e);
				} finally {
					if (localStoredImage != null) {
						new File(localStoredImage.getImagePath()).delete();
					}
				}
				return bmp;
			} catch (NotFoundException e) {
				DebugLog.logException(e);
				return null;
			}
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (getOwner().isVisible()) {
				consumer.onPhotoLoaded(originalUri, bitmap);
			} else {
				postponedBitmap = bitmap;
				postponedImageUri = originalUri;
			}

		}
	}
}
