/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.format.DateUtils;

import com.microsoft.embeddedsocial.account.UserAccount;
import com.microsoft.embeddedsocial.service.WorkerService;
import com.microsoft.embeddedsocial.service.ServiceAction;

/**
 * Periodically updates notification count.
 */
public class NotificationCountChecker {

	private static final long PERIOD = 15 * DateUtils.MINUTE_IN_MILLIS;

	private static long lastUpdateTime;

	private final Context context;
	private final Handler handler;
	private Runnable updateTask = this::updateNotificationCount;

	public NotificationCountChecker(Context context) {
		this.context = context;
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
		WorkerService.getLauncher(context).launchService(ServiceAction.UPDATE_NOTIFICATION_COUNT);
		handler.postDelayed(updateTask, PERIOD);
	}

	public static void reset() {
		lastUpdateTime = 0;
	}

}
