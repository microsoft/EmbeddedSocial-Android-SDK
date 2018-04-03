/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model.content.comments;

import com.microsoft.embeddedsocial.server.model.view.CommentView;

/**
 *
 */
public class GetCommentResponse {

	private CommentView comment;

	public GetCommentResponse(CommentView comment) {
		this.comment = comment;
	}

	public CommentView getComment() {
		return comment;
	}
}
