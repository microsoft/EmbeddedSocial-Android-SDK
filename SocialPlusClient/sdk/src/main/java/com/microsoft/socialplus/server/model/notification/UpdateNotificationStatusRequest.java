/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.notification;

import com.microsoft.autorest.models.PutNotificationsStatusRequest;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.socialplus.server.model.UserRequest;

import java.io.IOException;

import retrofit2.Response;

public class UpdateNotificationStatusRequest extends UserRequest {

	private final PutNotificationsStatusRequest request;

	public UpdateNotificationStatusRequest(String readActivityHandle) {
		request = new PutNotificationsStatusRequest();
		request.setReadActivityHandle(readActivityHandle);
	}

	@Override
	public Response send() throws ServiceException, IOException {
		ServiceResponse<Object> serviceResponse =
				NOTIFICATIONS.putNotificationsStatus(request, bearerToken);
		return serviceResponse.getResponse();
	}
}
