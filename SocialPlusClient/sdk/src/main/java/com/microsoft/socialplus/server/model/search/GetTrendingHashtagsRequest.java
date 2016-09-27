package com.microsoft.socialplus.server.model.search;

import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.microsoft.socialplus.server.model.UserRequest;

import java.io.IOException;
import java.util.List;

public class GetTrendingHashtagsRequest extends UserRequest {

    @Override
    public GetTrendingHashtagsResponse send() throws NetworkRequestException {
        ServiceResponse<List<String>> serviceResponse;
        try {
            serviceResponse = HASHTAGS.getTrendingHashtags(authorization);
        } catch (ServiceException|IOException e) {
            throw new NetworkRequestException(e.getMessage());
        }
        checkResponseCode(serviceResponse);

        return new GetTrendingHashtagsResponse(serviceResponse.getBody());
    }
}
