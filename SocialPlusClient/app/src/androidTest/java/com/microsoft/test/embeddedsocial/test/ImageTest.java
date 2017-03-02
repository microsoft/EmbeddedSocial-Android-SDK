/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.test.embeddedsocial.test;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.URLUtil;

import com.microsoft.embeddedsocial.R;
import com.microsoft.embeddedsocial.autorest.models.ImageType;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.server.ImageUploader;

public class ImageTest extends BaseRestServicesTest {

	public void testAddImage() throws Exception{
		Uri uri = resourceToUri(getContext(), R.drawable.es_logo);
		String imageUri = ImageUploader.uploadImage(getContext(), uri, ImageType.USERPHOTO);
		DebugLog.i(imageUri);
		assertTrue("URI is not valid", URLUtil.isValidUrl(imageUri));
	}

	private static Uri resourceToUri (Context context, int resID) {
		return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
				+ context.getResources().getResourcePackageName(resID) + '/'
				+ context.getResources().getResourceTypeName(resID) + '/'
				+ context.getResources().getResourceEntryName(resID));
	}
}
