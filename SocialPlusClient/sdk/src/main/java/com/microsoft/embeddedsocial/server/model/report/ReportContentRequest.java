/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model.report;

import com.microsoft.embeddedsocial.autorest.CommentReportsOperations;
import com.microsoft.embeddedsocial.autorest.CommentReportsOperationsImpl;
import com.microsoft.embeddedsocial.autorest.ReplyReportsOperations;
import com.microsoft.embeddedsocial.autorest.ReplyReportsOperationsImpl;
import com.microsoft.embeddedsocial.autorest.TopicReportsOperations;
import com.microsoft.embeddedsocial.autorest.TopicReportsOperationsImpl;
import com.microsoft.embeddedsocial.autorest.models.ContentType;
import com.microsoft.embeddedsocial.autorest.models.PostReportRequest;
import com.microsoft.embeddedsocial.autorest.models.Reason;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.UserRequest;

import java.io.IOException;

import retrofit2.Response;

public class ReportContentRequest extends UserRequest {

	private static final TopicReportsOperations TOPIC_REPORT;
	private static final CommentReportsOperations COMMENT_REPORT;
	private static final ReplyReportsOperations REPLY_REPORT;

	static {
		TOPIC_REPORT = new TopicReportsOperationsImpl(RETROFIT, CLIENT);
		COMMENT_REPORT = new CommentReportsOperationsImpl(RETROFIT, CLIENT);
		REPLY_REPORT = new ReplyReportsOperationsImpl(RETROFIT, CLIENT);
	}

	private PostReportRequest request;
	private String contentHandle;
	private ContentType contentType;

	public ReportContentRequest(ContentType contentType, String contentHandle, Reason reason) {
		if (contentType == null) {
			throw new IllegalArgumentException("Content type cannot be null");
		}
		if (contentType == ContentType.UNKNOWN) {
			throw new IllegalArgumentException("Content type cannot be unknown");
		}
		request = new PostReportRequest();
		request.setReason(reason);
		this.contentType = contentType;
		this.contentHandle = contentHandle;
	}

	@Override
	public Response send() throws NetworkRequestException {
		ServiceResponse<Object> serviceResponse;
		try {
			switch (contentType) {
				case TOPIC:
					serviceResponse = TOPIC_REPORT.postReport(contentHandle, request, authorization);
					break;
				case COMMENT:
					serviceResponse = COMMENT_REPORT.postReport(contentHandle, request, authorization);
					break;
				case REPLY:
					serviceResponse = REPLY_REPORT.postReport(contentHandle, request, authorization);
					break;
				default:
					throw new IllegalStateException("Unknown type for like");
			}
		} catch (ServiceException|IOException e) {
			throw new NetworkRequestException(e.getMessage());
		}
		checkResponseCode(serviceResponse);

		return serviceResponse.getResponse();
	}
}
