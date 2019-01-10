/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.service;

import com.microsoft.embeddedsocial.base.service.IServiceIntentProcessor;
import com.microsoft.embeddedsocial.base.service.IntentProcessor;
import com.microsoft.embeddedsocial.service.handler.LinkUserThirdPartyAccountHandler;
import com.microsoft.embeddedsocial.service.handler.UnlinkUserThirdPartyAccountHandler;
import com.microsoft.embeddedsocial.service.handler.UpdateNotificationCountHandler;

import android.content.Context;

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

        processor.registerIntentHandler(ServiceAction.UPDATE_NOTIFICATION_COUNT, new UpdateNotificationCountHandler());
        processor.registerIntentHandler(ServiceAction.LINK_USER_THIRD_PARTY_ACCOUNT, new LinkUserThirdPartyAccountHandler());
        processor.registerIntentHandler(ServiceAction.UNLINK_USER_THIRD_PARTY_ACCOUNT, new UnlinkUserThirdPartyAccountHandler());

        return processor;
    }
}
