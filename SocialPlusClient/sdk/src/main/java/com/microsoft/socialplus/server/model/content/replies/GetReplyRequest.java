/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.content.replies;

import com.microsoft.autorest.models.ReplyView;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;

import java.io.IOException;

public class GetReplyRequest extends GenericReplyRequest {
	public GetReplyRequest(String replyHandle) {
		super(replyHandle);
	}

	@Override
	public GetReplyResponse send() throws ServiceException, IOException {
		ServiceResponse<ReplyView> serviceResponse =
				REPLIES.getReply(replyHandle, bearerToken);
		return new GetReplyResponse(
				new com.microsoft.socialplus.server.model.view.ReplyView(serviceResponse.getBody()));
	}
}
