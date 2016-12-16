/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.StringRes;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.microsoft.embeddedsocial.event.sync.PostUploadFailedEvent;
import com.microsoft.embeddedsocial.event.sync.PostUploadedEvent;
import com.microsoft.embeddedsocial.event.sync.PushNotificationReceivedEvent;
import com.microsoft.embeddedsocial.base.event.EventBus;
import com.microsoft.embeddedsocial.sdk.R;
import com.squareup.otto.Subscribe;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Controls notifications shown by the application.
 */
public class NotificationController {

	private static final int BASE_PUSH_NOTIFICATION_ID = 0xCEEF_0;
	private static final int POST_UPLOAD_NOTIFICATION_ID = 0xBEEF_0;

	private final Context context;
	private final NotificationManagerCompat notificationManager;
	private final PendingIntent appLaunchIntent;

	private final Object eventListener = new Object() {

		private final AtomicInteger pushNotificationId = new AtomicInteger(BASE_PUSH_NOTIFICATION_ID);

		@SuppressWarnings("unused")
		@Subscribe
		public void onPostUploadFailed(PostUploadFailedEvent event) {
			int messageId = R.string.sp_message_failed_to_publish_post;
			Notification notification = buildBaseNotification(messageId)
				.setAutoCancel(true)
				.setOngoing(false)
				.build();
			notificationManager.notify(POST_UPLOAD_NOTIFICATION_ID, notification);
		}

		@SuppressWarnings("unused")
		@Subscribe
		public void onPostUploadSucceeded(PostUploadedEvent event) {
			notificationManager.cancel(POST_UPLOAD_NOTIFICATION_ID);
		}

		@SuppressWarnings("unused")
		@Subscribe
		public void onPushNotificationReceived(PushNotificationReceivedEvent event) {
			Notification notification = buildBaseNotification(event.getText())
				.setOngoing(false)
				.setAutoCancel(true)
				.build();
			notificationManager.notify(pushNotificationId.incrementAndGet(), notification);
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
		Intent intent = context.getPackageManager()
			.getLaunchIntentForPackage(context.getPackageName())
			.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		this.appLaunchIntent = PendingIntent.getActivity(context, POST_UPLOAD_NOTIFICATION_ID, intent,
			PendingIntent.FLAG_UPDATE_CURRENT);

		EventBus.register(eventListener);
	}

	private NotificationCompat.Builder buildBaseNotification(@StringRes int textId) {
		return buildBaseNotification(context.getString(textId));
	}

	private NotificationCompat.Builder buildBaseNotification(String text) {
		return new NotificationCompat.Builder(context)
			.setContentTitle(context.getString(R.string.sp_lib_name))
			.setContentText(text)
			.setTicker(text)
			.setContentIntent(appLaunchIntent)
			.setSmallIcon(R.drawable.sp_ic_bar_notification);
	}
}
