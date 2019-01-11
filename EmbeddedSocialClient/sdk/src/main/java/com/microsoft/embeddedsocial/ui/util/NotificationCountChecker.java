/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.util;

import com.microsoft.embeddedsocial.account.UserAccount;
import com.microsoft.embeddedsocial.service.worker.UpdateNotificationCountWorker;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.format.DateUtils;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

/**
 * Periodically updates notification count.
 */
public class NotificationCountChecker {

    private static final long PERIOD = 15 * DateUtils.MINUTE_IN_MILLIS;

    private static long lastUpdateTime;

    private final Handler handler;
    private Runnable updateTask = this::updateNotificationCount;

    public NotificationCountChecker() {
        handler = new Handler(Looper.getMainLooper());
    }

    public void onResume() {
        if (UserAccount.getInstance().isSignedIn()) {
            long timeSinceLastUpdate = SystemClock.elapsedRealtime() - lastUpdateTime;
            if (timeSinceLastUpdate > PERIOD || lastUpdateTime == 0) {
                updateNotificationCount();
            } else {
                handler.postDelayed(updateTask, PERIOD - timeSinceLastUpdate);
            }
        }
    }

    public void onPause() {
        handler.removeCallbacksAndMessages(null);
    }

    private void updateNotificationCount() {
        lastUpdateTime = SystemClock.elapsedRealtime();
        OneTimeWorkRequest workRequest =
                new OneTimeWorkRequest.Builder(UpdateNotificationCountWorker.class).build();
        WorkManager.getInstance().enqueue(workRequest);
        handler.postDelayed(updateTask, PERIOD);
    }

    public static void reset() {
        lastUpdateTime = 0;
    }

}
