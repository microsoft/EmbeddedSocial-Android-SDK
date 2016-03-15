/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.image;

import com.microsoft.socialplus.data.model.ImageType;
import com.microsoft.socialplus.server.model.UserRequest;

/**
 * Model for add image request.
 */
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class AddImageRequest extends UserRequest {

	private final int imageType;

	public AddImageRequest(ImageType imageType) {
		this.imageType = imageType.ordinal();
	}
}
