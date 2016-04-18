package com.microsoft.socialplus.server.model.search;

import com.microsoft.socialplus.autorest.models.FeedResponseTopicView;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.microsoft.socialplus.server.model.content.topics.TopicsListResponse;

import java.io.IOException;

public class SearchTopicsRequest extends SearchRequest {

    public SearchTopicsRequest(String query) {
        super(query);
    }

    @Override
    public TopicsListResponse send() throws NetworkRequestException {
        ServiceResponse<FeedResponseTopicView> serviceResponse;
        try {
            serviceResponse =
                    SEARCH.getTopics(query, getIntCursor(), getBatchSize(), appKey, bearerToken, null);
        } catch (ServiceException|IOException e) {
            throw new NetworkRequestException(e.getMessage());
        }
        checkResponseCode(serviceResponse);

        return new TopicsListResponse(serviceResponse.getBody());
    }
}
