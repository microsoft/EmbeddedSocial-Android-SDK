/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.service.handler;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import com.microsoft.embeddedsocial.base.service.IServiceIntentHandler;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.gcm.GcmTokenHolder;
import com.microsoft.embeddedsocial.service.ServiceAction;
import com.microsoft.embeddedsocial.service.WorkerService;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

/**
 * Registers the app with Firebase Cloud Messaging framework.
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
            DebugLog.i("obtaining new FCM token");
            FirebaseInstanceId instanceId = FirebaseInstanceId.getInstance();
            instanceId.getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    GcmTokenHolder.create(context).storeToken(instanceIdResult.getToken());
                    DebugLog.i("FCM token obtained successfully");

                    WorkerService.getLauncher(context).launchService(ServiceAction.SYNC_DATA);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    DebugLog.logException(e);
                }
            });
        }
    }

    @Override
    public void dispose() {
    }
}
