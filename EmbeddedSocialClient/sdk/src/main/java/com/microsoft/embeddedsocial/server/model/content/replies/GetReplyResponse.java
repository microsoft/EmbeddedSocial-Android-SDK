/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model.content.replies;

import com.microsoft.embeddedsocial.server.model.view.ReplyView;

public class GetReplyResponse {

	private ReplyView reply;

	public GetReplyResponse(ReplyView reply) {
		this.reply = reply;
	}

	public ReplyView getReply() {
		return reply;
	}
}
