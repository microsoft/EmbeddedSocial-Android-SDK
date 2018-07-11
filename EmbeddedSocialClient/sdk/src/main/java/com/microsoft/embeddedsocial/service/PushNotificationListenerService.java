/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.service;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.event.sync.PushNotificationReceivedEvent;

import java.util.Map;

/**
 * Is used to receive FCM push notifications.
 */
public class PushNotificationListenerService extends FirebaseMessagingService {
    private static final String EMBEDDED_SOCIAL_PUBLISHER = "EmbeddedSocial";
    private static final String KEY_MESSAGE_TEXT = "msg";
    private static final String KEY_PUBLISHER = "publisher";

    @Override
    public void onMessageReceived(RemoteMessage message) {
        super.onMessageReceived(message);
        Map<String, String> data = message.getData();
        DebugLog.i("received a push message");
        DebugLog.logObject(data);
        if (isSocialNotification(data)) {
            if (data.containsKey(KEY_MESSAGE_TEXT)) {
                new PushNotificationReceivedEvent(data.get(KEY_MESSAGE_TEXT)).submit();
            } else {
                // malformed push notification
                DebugLog.w("Received an ES push notification without a msg field");
            }
        }
    }

    /**
     * Returns true if the notification is from Embedded Social
     */
    public boolean isSocialNotification(Map<String, String> data) {
        String publisher = data.get(KEY_PUBLISHER);
        return EMBEDDED_SOCIAL_PUBLISHER.equals(publisher);
    }
}
