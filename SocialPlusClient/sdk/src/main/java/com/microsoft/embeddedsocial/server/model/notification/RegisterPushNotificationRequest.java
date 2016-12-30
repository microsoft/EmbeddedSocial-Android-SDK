/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model.notification;

import com.microsoft.embeddedsocial.autorest.models.PutPushRegistrationRequest;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.UserRequest;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.IOException;

import retrofit2.Response;

public class RegisterPushNotificationRequest extends UserRequest {

	private final String registrationID;
	private final PutPushRegistrationRequest request;

	public RegisterPushNotificationRequest(String registrationID, long lastUpdatedTime) {
		this.registrationID = registrationID;
		request = new PutPushRegistrationRequest();
		DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
		request.setLastUpdatedTime(fmt.print(lastUpdatedTime));
		request.setLanguage(language);
	}

	@Override
	public Response send() throws NetworkRequestException {
		ServiceResponse<Object> serviceResponse;
		try {
			serviceResponse = PUSH_REGISTRATION.putPushRegistration(platform, registrationID, request, authorization);
		} catch (ServiceException|IOException e) {
			throw new NetworkRequestException(e.getMessage());
		}
		checkResponseCode(serviceResponse);

		return serviceResponse.getResponse();
	}
}
