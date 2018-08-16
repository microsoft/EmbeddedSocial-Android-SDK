/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.notification;

import com.microsoft.embeddedsocial.base.event.EventBus;
import com.microsoft.embeddedsocial.event.sync.PostUploadFailedEvent;
import com.microsoft.embeddedsocial.event.sync.PostUploadedEvent;
import com.microsoft.embeddedsocial.event.sync.PushNotificationReceivedEvent;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.ui.activity.ActivityFeedActivity;
import com.squareup.otto.Subscribe;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.StringRes;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Controls notifications shown by the application.
 */
public class NotificationController {

    private static final int BASE_PUSH_NOTIFICATION_ID = 0xCEEF_0;
    private static final int POST_UPLOAD_NOTIFICATION_ID = 0xBEEF_0;

    private final Context context;
    private final NotificationManagerCompat notificationManager;

    private final Object eventListener = new Object() {

        private final AtomicInteger pushNotificationId = new AtomicInteger(BASE_PUSH_NOTIFICATION_ID);

        @SuppressWarnings("unused")
        @Subscribe
        public void onPostUploadFailed(PostUploadFailedEvent event) {
            int messageId = R.string.es_message_failed_to_publish_post;
            Intent intent = context.getPackageManager()
                    .getLaunchIntentForPackage(context.getPackageName())
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent appLaunchIntent = PendingIntent.getActivity(context, POST_UPLOAD_NOTIFICATION_ID, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder mBuilder = buildBaseNotification(messageId, appLaunchIntent);
            notificationManager.notify(POST_UPLOAD_NOTIFICATION_ID, mBuilder.build());
        }

        @SuppressWarnings("unused")
        @Subscribe
        public void onPostUploadSucceeded(PostUploadedEvent event) {
            notificationManager.cancel(POST_UPLOAD_NOTIFICATION_ID);
        }

        @SuppressWarnings("unused")
        @Subscribe
        public void onPushNotificationReceived(PushNotificationReceivedEvent event) {
            Intent intent = new Intent(context, ActivityFeedActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(ActivityFeedActivity.class);
            stackBuilder.addNextIntent(intent);
            PendingIntent pendingIntent = stackBuilder.getPendingIntent(pushNotificationId.get(),
                    PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder mBuilder = buildBaseNotification(event.getText(), pendingIntent);
            notificationManager.notify(pushNotificationId.incrementAndGet(), mBuilder.build());
        }
    };

    /**
     * Creates an instance.
     *
     * @param context valid context
     */
    public NotificationController(Context context) {
        this.context = context;
        this.notificationManager = NotificationManagerCompat.from(context);

        EventBus.register(eventListener);
    }

    private NotificationCompat.Builder buildBaseNotification(@StringRes int textId, PendingIntent pendingIntent) {
        return buildBaseNotification(context.getString(textId), pendingIntent);
    }

    /**
     * Creates a builder for push notifications ensuring they have a consistent look.
     *
     * @param text Text to display as the body of the notification.
     * @param pendingIntent Pending intent to send when the user clicks on the notification.
     * @return A builder for a notification prepopulated with a title, text, ticker, icon, and pending intent.
     */
    private NotificationCompat.Builder buildBaseNotification(String text, PendingIntent pendingIntent) {
        return new NotificationCompat.Builder(context)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(text)
            .setTicker(text)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.es_ic_bar_notification)
            .setOngoing(false)
            .setAutoCancel(true);
    }
}
