package com.microsoft.socialplus.server.model.search;

import com.microsoft.autorest.models.FeedResponseUserCompactView;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.socialplus.server.model.UsersListResponse;

import java.io.IOException;

public class SearchUsersRequest extends SearchRequest {

    public SearchUsersRequest(String query) {
        super(query);
    }

    @Override
    public UsersListResponse send() throws ServiceException, IOException {
        ServiceResponse<FeedResponseUserCompactView> serviceResponse =
                SEARCH.getUsers(query, getIntCursor(), getBatchSize(), appKey, bearerToken);
        return new UsersListResponse(serviceResponse.getBody());
    }
}
