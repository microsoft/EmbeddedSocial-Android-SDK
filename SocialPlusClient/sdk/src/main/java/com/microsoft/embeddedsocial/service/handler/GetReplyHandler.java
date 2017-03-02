/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.service.handler;

import android.content.Intent;

import com.microsoft.embeddedsocial.server.model.content.replies.GetReplyResponse;
import com.microsoft.embeddedsocial.actions.Action;
import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.base.event.EventBus;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.event.content.GetReplyEvent;
import com.microsoft.embeddedsocial.server.IContentService;
import com.microsoft.embeddedsocial.server.EmbeddedSocialServiceProvider;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.content.replies.GetReplyRequest;
import com.microsoft.embeddedsocial.service.IntentExtras;
import com.microsoft.embeddedsocial.service.ServiceAction;

/**
 * Get single reply.
 */
public class GetReplyHandler extends ActionHandler {
	@Override
	protected void handleAction(Action action, ServiceAction serviceAction, Intent intent) {
		IContentService contentService
				= GlobalObjectRegistry.getObject(EmbeddedSocialServiceProvider.class).getContentService();

		final String replyHandle = intent.getExtras().getString(IntentExtras.REPLY_HANDLE);

		try {
			final GetReplyRequest request = new GetReplyRequest(replyHandle);
			GetReplyResponse response = contentService.getReply(request);
			EventBus.post(new GetReplyEvent(response.getReply(), response.getReply() != null));
		} catch (NetworkRequestException e) {
			DebugLog.logException(e);
			action.fail(e.getMessage());
			EventBus.post(new GetReplyEvent(null, false));
		}
	}
}
