/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.service;

import android.content.Context;

import com.microsoft.socialplus.base.service.IServiceIntentProcessor;
import com.microsoft.socialplus.base.service.IntentProcessor;
import com.microsoft.socialplus.service.handler.BackgroundInitializationHandler;
import com.microsoft.socialplus.service.handler.CreateAccountHandler;
import com.microsoft.socialplus.service.handler.DeleteAccountHandler;
import com.microsoft.socialplus.service.handler.DeleteSearchHistoryHandler;
import com.microsoft.socialplus.service.handler.GetCommentHandler;
import com.microsoft.socialplus.service.handler.GetGcmIdHandler;
import com.microsoft.socialplus.service.handler.GetReplyHandler;
import com.microsoft.socialplus.service.handler.LinkUserThirdPartyAccountHandler;
import com.microsoft.socialplus.service.handler.RemoveFollowerHandler;
import com.microsoft.socialplus.service.handler.SignInHandler;
import com.microsoft.socialplus.service.handler.SignOutHandler;
import com.microsoft.socialplus.service.handler.SynchronizationHandler;
import com.microsoft.socialplus.service.handler.UnlinkUserThirdPartyAccountHandler;
import com.microsoft.socialplus.service.handler.UpdateAccountHandler;
import com.microsoft.socialplus.service.handler.UpdateNotificationCountHandler;

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
