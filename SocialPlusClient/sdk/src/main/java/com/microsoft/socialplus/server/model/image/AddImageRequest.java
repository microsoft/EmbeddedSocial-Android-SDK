/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.image;

import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.socialplus.autorest.models.ImageType;
import com.microsoft.socialplus.autorest.models.PostImageResponse;
import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.microsoft.socialplus.server.model.UserRequest;

import java.io.IOException;

/**
 * Model for add image request.
 */
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class AddImageRequest extends UserRequest {

	private final byte[] image;
	private final ImageType imageType;

	public AddImageRequest(byte[] image, ImageType imageType) {
		this.imageType = imageType;
		this.image = image;
	}

	@Override
	public String send() throws NetworkRequestException {
		ServiceResponse<PostImageResponse> serviceResponse;
		try {
			serviceResponse = IMAGES.postImage(imageType, bearerToken, image);
		} catch (ServiceException |IOException e) {
			throw new NetworkRequestException(e.getMessage());
		}
		checkResponseCode(serviceResponse);
		return serviceResponse.getBody().getBlobHandle();
	}
}
