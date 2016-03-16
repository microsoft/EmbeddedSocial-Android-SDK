package com.microsoft.socialplus.server.model.search;

import com.microsoft.autorest.models.FeedResponseUserProfileView;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.socialplus.server.model.FeedUserRequest;
import com.microsoft.socialplus.server.model.UsersListResponse;

import java.io.IOException;

public class GetPopularUsersRequest extends FeedUserRequest {

    @Override
    public UsersListResponse send() throws ServiceException, IOException {
        ServiceResponse<FeedResponseUserProfileView> serviceResponse =
                USERS.getPopularUsers(getIntCursor(), getBatchSize(), appKey, bearerToken);
        return new UsersListResponse(serviceResponse.getBody());
    }
}
