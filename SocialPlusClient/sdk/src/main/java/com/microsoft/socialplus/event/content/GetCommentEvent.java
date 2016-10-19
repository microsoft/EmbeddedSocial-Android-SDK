/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.event.content;

import com.microsoft.socialplus.base.event.AbstractEvent;
import com.microsoft.socialplus.base.event.HandlingThread;
import com.microsoft.socialplus.base.event.ThreadType;
import com.microsoft.socialplus.server.model.view.CommentView;

/**
 * Single comment was loaded.
 */
@HandlingThread(ThreadType.MAIN)
public class GetCommentEvent extends AbstractEvent {
	private final CommentView commentView;
	private final boolean result;

	public GetCommentEvent(CommentView commentView, boolean result) {
		this.commentView = commentView;
		this.result = result;
	}

	public CommentView getCommentView() {
		return commentView;
	}

	public boolean isResult() {
		return result;
	}
}
