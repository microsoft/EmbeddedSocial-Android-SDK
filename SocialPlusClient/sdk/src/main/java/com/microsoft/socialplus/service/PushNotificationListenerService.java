/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.service;

import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;
import com.microsoft.socialplus.base.utils.debug.DebugLog;
import com.microsoft.socialplus.event.sync.PushNotificationReceivedEvent;

/**
 * Is used to receive GCM push notifications.
 */
public class PushNotificationListenerService extends GcmListenerService {

	private static final String KEY_MESSAGE_TEXT = "msg";

	@Override
	public void onMessageReceived(String from, Bundle data) {
		super.onMessageReceived(from, data);
		DebugLog.i("received a push message");
		DebugLog.logBundle(data);
		new PushNotificationReceivedEvent(data.getString(KEY_MESSAGE_TEXT, "")).submit();
	}
}
