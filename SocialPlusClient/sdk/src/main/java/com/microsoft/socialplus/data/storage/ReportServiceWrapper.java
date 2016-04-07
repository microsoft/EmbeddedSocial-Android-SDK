package com.microsoft.socialplus.data.storage;

import com.microsoft.socialplus.server.IReportService;
import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.microsoft.socialplus.server.model.report.ReportContentRequest;
import com.microsoft.socialplus.server.model.report.ReportUserRequest;

import retrofit2.Response;

public class ReportServiceWrapper implements IReportService {

    @Override
    public Response reportContent(ReportContentRequest request) throws NetworkRequestException {
        return request.send();
    }

    @Override
    public Response reportUser(ReportUserRequest request) throws NetworkRequestException {
        return request.send();
    }
}
