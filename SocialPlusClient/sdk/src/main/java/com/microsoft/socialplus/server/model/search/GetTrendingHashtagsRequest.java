package com.microsoft.socialplus.server.model.search;

import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.socialplus.server.model.UserRequest;

import java.io.IOException;
import java.util.List;

public class GetTrendingHashtagsRequest extends UserRequest {

    @Override
    public GetTrendingHashtagsResponse send() throws ServiceException, IOException {
        ServiceResponse<List<String>> serviceResponse =
                HASHTAGS.getTrendingHashtags(appKey, bearerToken);
        return new GetTrendingHashtagsResponse(serviceResponse.getBody());
    }
}
