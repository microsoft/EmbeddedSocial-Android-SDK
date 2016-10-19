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
import com.microsoft.socialplus.event.content.GetCommentEvent;
import com.microsoft.socialplus.server.IContentService;
import com.microsoft.socialplus.server.SocialPlusServiceProvider;
import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.microsoft.socialplus.server.model.content.comments.GetCommentRequest;
import com.microsoft.socialplus.server.model.content.comments.GetCommentResponse;
import com.microsoft.socialplus.service.IntentExtras;
import com.microsoft.socialplus.service.ServiceAction;

/**
 * Get single comment.
 */
public class GetCommentHandler extends ActionHandler {
	@Override
	protected void handleAction(Action action, ServiceAction serviceAction, Intent intent) {
		IContentService contentService
				= GlobalObjectRegistry.getObject(SocialPlusServiceProvider.class).getContentService();

		final String commentHandle = intent.getExtras().getString(IntentExtras.COMMENT_HANDLE);

		try {
			final GetCommentRequest request = new GetCommentRequest(commentHandle);
			GetCommentResponse response = contentService.getComment(request);
			EventBus.post(new GetCommentEvent(response.getComment(), response.getComment() != null));
		} catch (NetworkRequestException e) {
			DebugLog.logException(e);
			action.fail(e.getMessage());
			EventBus.post(new GetCommentEvent(null, false));
		}
	}
}
