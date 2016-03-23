/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.service.handler;

import android.content.Intent;

import com.microsoft.socialplus.autorest.models.CountResponse;
import com.microsoft.socialplus.base.GlobalObjectRegistry;
import com.microsoft.socialplus.base.service.IServiceIntentHandler;
import com.microsoft.socialplus.base.utils.debug.DebugLog;
import com.microsoft.socialplus.data.Preferences;
import com.microsoft.socialplus.server.INotificationService;
import com.microsoft.socialplus.server.SocialPlusServiceProvider;
import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.microsoft.socialplus.server.model.notification.GetNotificationCountRequest;
import com.microsoft.socialplus.service.ServiceAction;

/**
 * Updates notification count from the server.
 */
public class UpdateNotificationCountHandler implements IServiceIntentHandler<ServiceAction> {
	@Override
	public void handleIntent(ServiceAction action, Intent intent) {
		INotificationService server = GlobalObjectRegistry.getObject(SocialPlusServiceProvider.class).getNotificationService();
		try {
			CountResponse response = server.getNotificationCount(new GetNotificationCountRequest());
			long notificationCount = response.getCount();
			Preferences.getInstance().setNotificationCount(notificationCount);
		} catch (NetworkRequestException e) {
			DebugLog.logException(e);
		}
	}

	@Override
	public void dispose() {

	}
}
