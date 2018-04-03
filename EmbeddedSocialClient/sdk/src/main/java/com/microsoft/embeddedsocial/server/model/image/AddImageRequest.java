/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model.image;

import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.embeddedsocial.autorest.ImagesOperations;
import com.microsoft.embeddedsocial.autorest.ImagesOperationsImpl;
import com.microsoft.embeddedsocial.autorest.models.ImageType;
import com.microsoft.embeddedsocial.autorest.models.PostImageResponse;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.UserRequest;

import java.io.IOException;

/**
 * Model for add image request.
 */
public class AddImageRequest extends UserRequest {
	private static final ImagesOperations IMAGES = new ImagesOperationsImpl(RETROFIT, CLIENT);

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
			serviceResponse = IMAGES.postImage(imageType, authorization, image);
		} catch (ServiceException |IOException e) {
			throw new NetworkRequestException(e.getMessage());
		}
		checkResponseCode(serviceResponse);
		return serviceResponse.getBody().getBlobHandle();
	}
}
