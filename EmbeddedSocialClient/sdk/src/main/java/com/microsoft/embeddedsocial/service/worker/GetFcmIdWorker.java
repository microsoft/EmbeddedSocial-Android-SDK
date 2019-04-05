/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.service.worker;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.fcm.FcmTokenHolder;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 * Gets FCM tokens and stores them
 */
public class GetFcmIdWorker extends Worker {
    private final Context context;
    private final FcmTokenHolder tokenHolder;

    public GetFcmIdWorker(Context context, WorkerParameters workerParameters) {
        super(context, workerParameters);
        this.context = context;
        this.tokenHolder = FcmTokenHolder.create(context);
    }

    @Override
    public Result doWork() {
        if (!tokenHolder.hasValidToken()) {
            DebugLog.i("obtaining new FCM token");
            FirebaseInstanceId instanceId = FirebaseInstanceId.getInstance();
            instanceId.getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    FcmTokenHolder.create(context).storeToken(instanceIdResult.getToken());
                    DebugLog.i("FCM token obtained successfully");

                    OneTimeWorkRequest backgroundInit = new OneTimeWorkRequest.Builder(SynchronizationWorker.class).build();
                    WorkManager.getInstance().enqueue(backgroundInit);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    DebugLog.logException(e);
                }
            });
        }

        return Result.success();
    }
}
