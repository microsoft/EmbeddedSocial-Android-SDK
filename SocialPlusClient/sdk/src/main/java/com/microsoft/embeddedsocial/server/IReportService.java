/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server;

import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.report.ReportContentRequest;
import com.microsoft.embeddedsocial.server.model.report.ReportUserRequest;

import retrofit2.Response;

/**
 * Interface for reporting users/content
 */
public interface IReportService {

	Response reportContent(ReportContentRequest request)
			throws NetworkRequestException;

	Response reportUser(ReportUserRequest request)
			throws NetworkRequestException;
}
