/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.service;

import android.content.Context;

import com.microsoft.embeddedsocial.service.handler.UpdateAccountHandler;
import com.microsoft.embeddedsocial.base.service.IServiceIntentProcessor;
import com.microsoft.embeddedsocial.base.service.IntentProcessor;
import com.microsoft.embeddedsocial.service.handler.BackgroundInitializationHandler;
import com.microsoft.embeddedsocial.service.handler.CreateAccountHandler;
import com.microsoft.embeddedsocial.service.handler.DeleteAccountHandler;
import com.microsoft.embeddedsocial.service.handler.DeleteSearchHistoryHandler;
import com.microsoft.embeddedsocial.service.handler.GetCommentHandler;
import com.microsoft.embeddedsocial.service.handler.GetGcmIdHandler;
import com.microsoft.embeddedsocial.service.handler.GetReplyHandler;
import com.microsoft.embeddedsocial.service.handler.LinkUserThirdPartyAccountHandler;
import com.microsoft.embeddedsocial.service.handler.RemoveFollowerHandler;
import com.microsoft.embeddedsocial.service.handler.SignInHandler;
import com.microsoft.embeddedsocial.service.handler.SignOutHandler;
import com.microsoft.embeddedsocial.service.handler.SynchronizationHandler;
import com.microsoft.embeddedsocial.service.handler.UnlinkUserThirdPartyAccountHandler;
import com.microsoft.embeddedsocial.service.handler.UpdateNotificationCountHandler;

/**
 * Builds intents processor.
 */
public class IntentProcessorFactory {

	private static final int WORKER_THREADS = 2;

	private final Context context;

	IntentProcessorFactory(Context context) {
		this.context = context;
	}

	IServiceIntentProcessor createIntentProcessor() {
		IntentProcessor<ServiceAction> processor = new IntentProcessor<>(context, ServiceAction.class, WORKER_THREADS);

		processor.registerIntentHandler(ServiceAction.SIGN_IN, new SignInHandler(context));
		processor.registerIntentHandler(ServiceAction.SIGN_OUT, new SignOutHandler(context));
		processor.registerIntentHandler(ServiceAction.SYNC_DATA, new SynchronizationHandler(context));
		processor.registerIntentHandler(ServiceAction.CREATE_ACCOUNT, new CreateAccountHandler(context));
		processor.registerIntentHandler(ServiceAction.UPDATE_ACCOUNT, new UpdateAccountHandler(context));
		processor.registerIntentHandler(ServiceAction.GCM_REGISTER, new GetGcmIdHandler(context));
		processor.registerIntentHandler(ServiceAction.BACKGROUND_INIT, new BackgroundInitializationHandler(context));
		processor.registerIntentHandler(ServiceAction.UPDATE_NOTIFICATION_COUNT, new UpdateNotificationCountHandler());
		processor.registerIntentHandler(ServiceAction.GET_COMMENT, new GetCommentHandler());
		processor.registerIntentHandler(ServiceAction.GET_REPLY, new GetReplyHandler());
		processor.registerIntentHandler(ServiceAction.DELETE_SEARCH_HISTORY, new DeleteSearchHistoryHandler());
		processor.registerIntentHandler(ServiceAction.DELETE_ACCOUNT, new DeleteAccountHandler());
		processor.registerIntentHandler(ServiceAction.LINK_USER_THIRD_PARTY_ACCOUNT, new LinkUserThirdPartyAccountHandler());
		processor.registerIntentHandler(ServiceAction.UNLINK_USER_THIRD_PARTY_ACCOUNT, new UnlinkUserThirdPartyAccountHandler());
		processor.registerIntentHandler(ServiceAction.REMOVE_FOLLOWER, new RemoveFollowerHandler());

		return processor;
	}
}
