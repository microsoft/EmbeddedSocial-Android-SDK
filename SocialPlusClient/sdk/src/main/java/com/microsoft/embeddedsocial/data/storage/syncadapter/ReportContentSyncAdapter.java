/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage.syncadapter;

import com.j256.ormlite.dao.Dao;
import com.microsoft.embeddedsocial.data.storage.model.ReportContentOperation;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.report.ReportContentRequest;
import com.microsoft.embeddedsocial.server.model.report.ReportUserRequest;
import com.microsoft.embeddedsocial.server.sync.exception.SynchronizationException;
import com.microsoft.embeddedsocial.autorest.models.ContentType;
import com.microsoft.embeddedsocial.server.IReportService;

/**
 * Uploads operations that report content or users as inappropriate.
 */
public class ReportContentSyncAdapter extends AbstractAutoCleanupSyncAdapter<ReportContentOperation> {

	public ReportContentSyncAdapter(ReportContentOperation operation,
	                                Dao<ReportContentOperation, ?> dao) {

		super(operation, dao);
	}

	@Override
	protected void onSynchronize(ReportContentOperation item)
		throws NetworkRequestException, SynchronizationException {

		IReportService service = getServiceProvider().getReportService();

		if (item.isForUser()) {
			reportUser(service, item);
		} else {
			reportContent(service, item);
		}
	}

	private void reportContent(IReportService service, ReportContentOperation item)
		throws NetworkRequestException {

		ReportContentRequest request = new ReportContentRequest(
			ContentType.fromValue(item.getContentType()),
			item.getContentHandle(),
			item.getReason()
		);
		service.reportContent(request);
	}

	private void reportUser(IReportService service, ReportContentOperation item)
		throws NetworkRequestException {

		ReportUserRequest request = new ReportUserRequest(
			item.getContentHandle(),
			item.getReason()
		);
		service.reportUser(request);
	}
}
