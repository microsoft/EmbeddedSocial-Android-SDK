/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model.content.replies;

/**
 *
 */
public class AddReplyResponse {

	private String replyHandle;

	public AddReplyResponse(String replyHandle) {
		this.replyHandle = replyHandle;
	}

	public String getReplyHandle() {
		return replyHandle;
	}
}
