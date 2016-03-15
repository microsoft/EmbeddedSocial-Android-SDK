/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.content.replies;

import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;

import java.io.IOException;

import retrofit2.Response;

public class RemoveReplyRequest extends GenericReplyRequest {

	public RemoveReplyRequest(String replyHandle) {
		super(replyHandle);
	}

	@Override
	public Response send() throws ServiceException, IOException {
		ServiceResponse<Object> serviceResponse =
				REPLIES.deleteReply(replyHandle, bearerToken);
		return serviceResponse.getResponse();
	}
}
