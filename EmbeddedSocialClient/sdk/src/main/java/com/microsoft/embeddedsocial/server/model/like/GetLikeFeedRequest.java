/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model.like;

import com.microsoft.embeddedsocial.autorest.models.ContentType;
import com.microsoft.embeddedsocial.autorest.models.FeedResponseUserCompactView;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.FeedUserRequest;
import com.microsoft.embeddedsocial.server.model.UsersListResponse;

import java.io.IOException;

public class GetLikeFeedRequest extends FeedUserRequest {

	private final String contentHandle;
	private final ContentType contentType;

	public GetLikeFeedRequest(String contentHandle, ContentType contentType) {
		if (contentType == ContentType.UNKNOWN) {
			throw new IllegalArgumentException("Content type cannot be unknown");
		}
		this.contentHandle = contentHandle;
		this.contentType = contentType;
	}

	public String getContentHandle() {
		return contentHandle;
	}

	public ContentType getContentType() {
		return contentType;
	}

	public UsersListResponse send() throws NetworkRequestException {
		ServiceResponse<FeedResponseUserCompactView> serviceResponse;
		String cursor = getCursor();
		int batchSize = getBatchSize();
		try {
			switch (contentType) {
				case TOPIC:
					serviceResponse = TOPIC_LIKES.getLikes(contentHandle, authorization,
							cursor, batchSize);
					break;
				case COMMENT:
					serviceResponse = COMMENT_LIKES.getLikes(contentHandle, authorization,
							cursor, batchSize);
					break;
				case REPLY:
					serviceResponse = REPLY_LIKES.getLikes(contentHandle, authorization,
							cursor, batchSize);
					break;
				default:
					throw new IllegalStateException("Unknown type for like");
			}
		} catch (ServiceException|IOException e) {
			throw new NetworkRequestException(e.getMessage());
		}
		checkResponseCode(serviceResponse);

		return new UsersListResponse(serviceResponse.getBody());
	}
}
