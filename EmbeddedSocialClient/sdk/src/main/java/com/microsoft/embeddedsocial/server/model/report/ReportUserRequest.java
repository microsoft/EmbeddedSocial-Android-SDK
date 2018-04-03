/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model.report;

import com.microsoft.embeddedsocial.autorest.UserReportsOperations;
import com.microsoft.embeddedsocial.autorest.UserReportsOperationsImpl;
import com.microsoft.embeddedsocial.autorest.models.PostReportRequest;
import com.microsoft.embeddedsocial.autorest.models.Reason;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.UserRequest;

import java.io.IOException;

import retrofit2.Response;

public class ReportUserRequest extends UserRequest {

	private static final UserReportsOperations USER_REPORTS;

	static {
		USER_REPORTS = new UserReportsOperationsImpl(RETROFIT, CLIENT);
	}
	private final String reportUserHandle;
	private final PostReportRequest request;

	public ReportUserRequest(String reportUserHandle, Reason reason) {
		request = new PostReportRequest();
		request.setReason(reason);
		this.reportUserHandle = reportUserHandle;
	}

	@Override
	public Response send() throws NetworkRequestException {
		ServiceResponse<Object> serviceResponse;
		try {
			serviceResponse = USER_REPORTS.postReport(reportUserHandle, request, authorization);
		} catch (ServiceException|IOException e) {
			throw new NetworkRequestException(e.getMessage());
		}
		checkResponseCode(serviceResponse);

		return serviceResponse.getResponse();
	}
}
