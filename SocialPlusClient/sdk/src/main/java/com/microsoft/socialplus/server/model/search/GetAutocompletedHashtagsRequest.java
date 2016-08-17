package com.microsoft.socialplus.server.model.search;

import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.socialplus.server.exception.NetworkRequestException;

import java.io.IOException;
import java.util.List;

public class GetAutocompletedHashtagsRequest extends SearchRequest {

    public GetAutocompletedHashtagsRequest(String query) {
        super(query);
    }

    @Override
    public AutocompletedHashtagsResponse send() throws NetworkRequestException {
        ServiceResponse<List<String>> serviceResponse;
        try {
            serviceResponse = HASHTAGS.getAutocompletedHashtags(query, authorization);
        } catch (ServiceException|IOException e) {
            throw new NetworkRequestException(e.getMessage());
        }
        checkResponseCode(serviceResponse);

        return new AutocompletedHashtagsResponse(serviceResponse.getBody());
    }
}
