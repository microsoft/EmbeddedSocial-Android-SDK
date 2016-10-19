/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.service.handler;

import android.content.Intent;

import com.microsoft.socialplus.actions.Action;
import com.microsoft.socialplus.base.GlobalObjectRegistry;
import com.microsoft.socialplus.base.event.EventBus;
import com.microsoft.socialplus.base.utils.debug.DebugLog;
import com.microsoft.socialplus.event.content.GetReplyEvent;
import com.microsoft.socialplus.server.IContentService;
import com.microsoft.socialplus.server.SocialPlusServiceProvider;
import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.microsoft.socialplus.server.model.content.replies.GetReplyRequest;
import com.microsoft.socialplus.server.model.content.replies.GetReplyResponse;
import com.microsoft.socialplus.service.IntentExtras;
import com.microsoft.socialplus.service.ServiceAction;

/**
 * Get single reply.
 */
public class GetReplyHandler extends ActionHandler {
	@Override
	protected void handleAction(Action action, ServiceAction serviceAction, Intent intent) {
		IContentService contentService
				= GlobalObjectRegistry.getObject(SocialPlusServiceProvider.class).getContentService();

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
