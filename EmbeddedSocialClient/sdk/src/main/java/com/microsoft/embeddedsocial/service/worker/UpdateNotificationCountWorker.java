/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.service.worker;

import com.microsoft.embeddedsocial.autorest.models.CountResponse;
import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.data.Preferences;
import com.microsoft.embeddedsocial.server.EmbeddedSocialServiceProvider;
import com.microsoft.embeddedsocial.server.INotificationService;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.notification.GetNotificationCountRequest;

import android.content.Context;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 * Updates notification count from the server.
 */
public class UpdateNotificationCountWorker extends Worker {
    public UpdateNotificationCountWorker(Context context, WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @Override
    public Result doWork() {
        INotificationService server = GlobalObjectRegistry.getObject(EmbeddedSocialServiceProvider.class).getNotificationService();
        try {
            CountResponse response = server.getNotificationCount(new GetNotificationCountRequest());
            long notificationCount = response.getCount();
            Preferences.getInstance().setNotificationCount(notificationCount);
        } catch (NetworkRequestException e) {
            DebugLog.logException(e);
            return Result.failure();
        }

        return Result.success();
    }
}
