/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.service.worker;

import android.content.Context;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class BackgroundInitializationWorker extends Worker {

    public BackgroundInitializationWorker(Context context, WorkerParameters workerParameters) {
        super(context, workerParameters);
    }

    @Override
    public Result doWork() {
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(GetFcmIdWorker.class).build();
        WorkManager.getInstance().enqueue(workRequest);

        return Result.success();
    }
}
