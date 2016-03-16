package com.microsoft.socialplus.server.model.search;

import com.microsoft.autorest.models.FeedResponseTopicView;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.socialplus.server.model.content.topics.TopicsListResponse;

import java.io.IOException;

public class SearchTopicsRequest extends SearchRequest {

    public SearchTopicsRequest(String query) {
        super(query);
    }

    @Override
    public TopicsListResponse send() throws ServiceException, IOException {
        ServiceResponse<FeedResponseTopicView> serviceResponse =
                SEARCH.getTopics(query, getIntCursor(), getBatchSize(), appKey, bearerToken);
        return new TopicsListResponse(serviceResponse.getBody());
    }
}
