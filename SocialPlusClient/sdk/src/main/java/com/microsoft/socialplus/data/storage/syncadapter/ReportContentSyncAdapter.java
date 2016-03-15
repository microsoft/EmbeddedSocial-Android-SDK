/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.data.storage.syncadapter;

import com.j256.ormlite.dao.Dao;
import com.microsoft.autorest.models.ContentType;
import com.microsoft.socialplus.data.storage.model.ReportContentOperation;
import com.microsoft.socialplus.server.IReportService;
import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.microsoft.socialplus.server.model.report.ReportContentRequest;
import com.microsoft.socialplus.server.model.report.ReportUserRequest;
import com.microsoft.socialplus.server.sync.exception.SynchronizationException;

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
			ContentType.valueOf(item.getContentType()),
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
