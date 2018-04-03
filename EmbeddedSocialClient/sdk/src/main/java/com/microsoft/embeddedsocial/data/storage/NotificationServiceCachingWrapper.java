/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage;

import android.content.Context;
import android.text.TextUtils;

import com.microsoft.embeddedsocial.data.Preferences;
import com.microsoft.embeddedsocial.data.storage.request.wrapper.AbstractBatchRequestWrapper;
import com.microsoft.embeddedsocial.server.INotificationService;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.notification.GetNotificationCountRequest;
import com.microsoft.embeddedsocial.server.model.notification.GetNotificationFeedRequest;
import com.microsoft.embeddedsocial.server.model.notification.GetNotificationFeedResponse;
import com.microsoft.embeddedsocial.server.model.notification.RegisterPushNotificationRequest;
import com.microsoft.embeddedsocial.server.model.notification.UnRegisterPushNotificationRequest;
import com.microsoft.embeddedsocial.server.model.notification.UpdateNotificationStatusRequest;
import com.microsoft.embeddedsocial.server.model.view.ActivityView;
import com.microsoft.embeddedsocial.service.ServiceAction;
import com.microsoft.embeddedsocial.service.WorkerService;
import com.microsoft.embeddedsocial.autorest.models.CountResponse;

import java.sql.SQLException;

import retrofit2.Response;

/**
 * Provides transparent cache implementation of top of {@linkplain INotificationService}.
 */
public class NotificationServiceCachingWrapper implements INotificationService {

	private final GetNotificationFeedWrapper notificationFeedWrapper = new GetNotificationFeedWrapper();
	private final Context context;
	private final ActivityCache activityCache;

	/**
	 * Creates an instance.
	 * @param context           valid context
	 */
	public NotificationServiceCachingWrapper(Context context) {
		this.context = context;
		this.activityCache = new ActivityCache(context);
	}

	@Override
	public CountResponse getNotificationCount(GetNotificationCountRequest request)
		throws NetworkRequestException {

		return request.send();
	}

	@Override
	public GetNotificationFeedResponse getNotificationFeed(GetNotificationFeedRequest request)
		throws NetworkRequestException {

		return notificationFeedWrapper.getResponse(request);
	}

	@Override
	public Response registerPushNotification(RegisterPushNotificationRequest request)
		throws NetworkRequestException {

		return request.send();
	}

	@Override
	public Response unregisterPushNotification(UnRegisterPushNotificationRequest request)
		throws NetworkRequestException {

		return request.send();
	}

	@Override
	public Response updateNotificationStatus(UpdateNotificationStatusRequest request)
		throws NetworkRequestException {

		return request.send();
	}

	private class GetNotificationFeedWrapper
		extends AbstractBatchRequestWrapper<GetNotificationFeedRequest, GetNotificationFeedResponse> {

		@Override
		protected GetNotificationFeedResponse getCachedResponse(GetNotificationFeedRequest request)
			throws SQLException {

			return activityCache.getNotificationFeedResponse();
		}

		@Override
		protected GetNotificationFeedResponse getNetworkResponse(GetNotificationFeedRequest request)
			throws NetworkRequestException {

			GetNotificationFeedResponse response = request.send();

			for (ActivityView activityView : response.getData()) {
				activityView.setUnread(activityCache.isActivityUnread(activityView.getHandle()));
			}

			if (isFirstDataRequest(request)) {
				updateLastActivityHandle(response);
				Preferences.getInstance().resetNotificationCount();
			}

			return response;
		}

		private void updateLastActivityHandle(GetNotificationFeedResponse response) {
			String deliveredActivityHandle = response.getDeliveredActivityHandle();
			String latestHandle;

			if (!response.getData().isEmpty()) {
				String firstItemHandle = response.getData().get(0).getHandle();

				if (!TextUtils.isEmpty(response.getDeliveredActivityHandle())) {
					latestHandle = firstItemHandle.compareTo(deliveredActivityHandle) < 0
						? firstItemHandle : deliveredActivityHandle;
				} else {
					latestHandle = firstItemHandle;
				}
			} else {
				latestHandle = deliveredActivityHandle;
			}

			if (activityCache.storeLastActivityHandle(latestHandle)) {
				launchSync();
			}
		}

		@Override
		protected void storeResponse(GetNotificationFeedRequest request, GetNotificationFeedResponse response)
			throws SQLException {

			activityCache.storeActivityFeed(
				ActivityCache.ActivityFeedType.NOTIFICATIONS,
				response.getData(),
				isFirstDataRequest(request)
			);
		}
	}

	private void launchSync() {
		WorkerService.getLauncher(context).launchService(ServiceAction.SYNC_DATA);
	}
}
