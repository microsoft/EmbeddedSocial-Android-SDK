package com.microsoft.socialplus.server.model.search;

import com.microsoft.autorest.models.FeedResponseUserProfileView;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.microsoft.socialplus.server.model.FeedUserRequest;
import com.microsoft.socialplus.server.model.UsersListResponse;

import java.io.IOException;

public class GetPopularUsersRequest extends FeedUserRequest {

    @Override
    public UsersListResponse send() throws NetworkRequestException {
        ServiceResponse<FeedResponseUserProfileView> serviceResponse;
        try {
            serviceResponse = USERS.getPopularUsers(getIntCursor(), getBatchSize(), appKey, bearerToken);
        } catch (ServiceException|IOException e) {
            throw new NetworkRequestException(e.getMessage());
        }
        checkResponseCode(serviceResponse);
        return new UsersListResponse(serviceResponse.getBody());
    }
}
