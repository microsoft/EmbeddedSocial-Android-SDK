/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.event.content;

import com.microsoft.socialplus.base.event.AbstractEvent;
import com.microsoft.socialplus.base.event.HandlingThread;
import com.microsoft.socialplus.base.event.ThreadType;
import com.microsoft.socialplus.server.model.view.ReplyView;

/**
 * Single reply was loaded.
 */
@HandlingThread(ThreadType.MAIN)
public class GetReplyEvent extends AbstractEvent {
	private final ReplyView replyView;
	private final boolean result;

	public GetReplyEvent(ReplyView replyView, boolean result) {
		this.replyView = replyView;
		this.result = result;
	}

	public ReplyView getReplyView() {
		return replyView;
	}

	public boolean isResult() {
		return result;
	}
}
