/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.notification;

import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.socialplus.server.model.OperatingSystemType;
import com.microsoft.socialplus.server.model.UserRequest;

import java.io.IOException;

import retrofit2.Response;

public class UnRegisterPushNotificationRequest extends UserRequest {

	private final String registrationID;

	public UnRegisterPushNotificationRequest(String registrationID) {
		this.registrationID = registrationID;
	}

	@Override
	public Response send() throws ServiceException, IOException {
		ServiceResponse<Object> serviceResponse =
				PUSH_REGISTRATION.deletePushRegistration(platform, registrationID, bearerToken);
		return serviceResponse.getResponse();
	}
}
