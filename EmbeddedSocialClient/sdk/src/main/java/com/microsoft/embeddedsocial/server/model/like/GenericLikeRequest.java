/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model.like;

import com.microsoft.embeddedsocial.autorest.models.ContentType;
import com.microsoft.embeddedsocial.server.model.UserRequest;

public class GenericLikeRequest extends UserRequest {

	protected final String contentHandle;
	protected final ContentType contentType;

	public GenericLikeRequest(String contentHandle, ContentType contentType) {
		if (contentType == ContentType.UNKNOWN) {
			throw new IllegalArgumentException("Content type cannot be unknown");
		}
		this.contentHandle = contentHandle;
		this.contentType = contentType;
	}
}
