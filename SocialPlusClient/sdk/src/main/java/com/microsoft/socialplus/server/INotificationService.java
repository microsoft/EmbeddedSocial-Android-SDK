/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.server;

import com.microsoft.socialplus.autorest.models.CountResponse;
import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.microsoft.socialplus.server.model.FeedUserRequest;
import com.microsoft.socialplus.server.model.notification.GetNotificationCountRequest;
import com.microsoft.socialplus.server.model.notification.GetNotificationFeedRequest;
import com.microsoft.socialplus.server.model.notification.GetNotificationFeedResponse;
import com.microsoft.socialplus.server.model.notification.RegisterPushNotificationRequest;
import com.microsoft.socialplus.server.model.notification.UnRegisterPushNotificationRequest;
import com.microsoft.socialplus.server.model.notification.UpdateNotificationStatusRequest;

import retrofit2.Response;

/**
 * Interface for notifications management
 */
public interface INotificationService {


	CountResponse getNotificationCount(GetNotificationCountRequest request)
			throws NetworkRequestException;

	GetNotificationFeedResponse getNotificationFeed(GetNotificationFeedRequest request)
			throws NetworkRequestException;

	Response registerPushNotification(RegisterPushNotificationRequest request)
			throws NetworkRequestException;

	Response unregisterPushNotification(UnRegisterPushNotificationRequest request)
			throws NetworkRequestException;

	Response updateNotificationStatus(UpdateNotificationStatusRequest request)
			throws NetworkRequestException;
}
