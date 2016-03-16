package com.microsoft.socialplus.server.model.search;

import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;

import java.io.IOException;
import java.util.List;

public class GetAutocompletedHashtagsRequest extends SearchRequest {

    public GetAutocompletedHashtagsRequest(String query) {
        super(query);
    }

    @Override
    public AutocompletedHashtagsResponse send() throws ServiceException, IOException {
        ServiceResponse<List<String>> serviceResponse =
                HASHTAGS.getAutocompletedHashtags(query, appKey, bearerToken);
        return new AutocompletedHashtagsResponse(serviceResponse.getBody());
    }
}
