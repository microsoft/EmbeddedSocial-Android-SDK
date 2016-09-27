package com.microsoft.socialplus.server.model.search;

import com.microsoft.socialplus.autorest.models.FeedResponseUserCompactView;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.microsoft.socialplus.server.model.UsersListResponse;

import java.io.IOException;

public class SearchUsersRequest extends SearchRequest {

    public SearchUsersRequest(String query) {
        super(query);
    }

    @Override
    public UsersListResponse send() throws NetworkRequestException {
        ServiceResponse<FeedResponseUserCompactView> serviceResponse;
        try {
            serviceResponse =
                    SEARCH.getUsers(query, authorization, getIntCursor(), getBatchSize());
        } catch (ServiceException|IOException e) {
            throw new NetworkRequestException(e.getMessage());
        }
        checkResponseCode(serviceResponse);

        return new UsersListResponse(serviceResponse.getBody());
    }
}
