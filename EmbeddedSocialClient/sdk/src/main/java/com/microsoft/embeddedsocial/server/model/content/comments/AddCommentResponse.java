/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model.content.comments;

/**
 *
 */
public class AddCommentResponse {

	private String commentHandle;

	public AddCommentResponse(String commentHandle) {
		this.commentHandle = commentHandle;
	}

	public String getCommentHandle() {
		return commentHandle;
	}
}
