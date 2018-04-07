/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.service.handler;

import android.content.Context;
import android.content.Intent;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.microsoft.embeddedsocial.base.service.IServiceIntentHandler;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.gcm.GcmTokenHolder;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.service.WorkerService;
import com.microsoft.embeddedsocial.service.ServiceAction;

import java.io.IOException;

/**
 * Registers the app with Google Cloud Messaging framework.
 */
public class GetGcmIdHandler implements IServiceIntentHandler<ServiceAction> {

    private final Context context;
    private final GcmTokenHolder tokenHolder;

    public GetGcmIdHandler(Context context) {
        this.context = context;
        this.tokenHolder = GcmTokenHolder.create(context);
    }

    @Override
    public void handleIntent(ServiceAction action, Intent intent) {
        if (!tokenHolder.hasValidToken()) {
            DebugLog.i("obtaining new GCM token");
            InstanceID instanceID = InstanceID.getInstance(context);
            try {
                String token = instanceID.getToken(
                    context.getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE,
                    null
                );
                GcmTokenHolder.create(context).storeToken(token);
                DebugLog.i("GCM token obtained successfully");
            } catch (IOException e) {
                DebugLog.logException(e);
            }
        }
        WorkerService.getLauncher(context).launchService(ServiceAction.SYNC_DATA);
    }

    @Override
    public void dispose() {
    }
}
