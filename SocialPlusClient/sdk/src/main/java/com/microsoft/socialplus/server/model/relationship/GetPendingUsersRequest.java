package com.microsoft.socialplus.server.model.relationship;

import com.microsoft.autorest.models.FeedResponseUserCompactView;
        import com.microsoft.rest.ServiceException;
        import com.microsoft.rest.ServiceResponse;
        import com.microsoft.socialplus.server.model.FeedUserRequest;
        import com.microsoft.socialplus.server.model.UsersListResponse;

        import java.io.IOException;

public class GetPendingUsersRequest extends FeedUserRequest {

    @Override
    public UsersListResponse send() throws ServiceException, IOException {
        ServiceResponse<FeedResponseUserCompactView> serviceResponse =
                PENDING.getPendingUsers(bearerToken, getCursor(), getBatchSize());
        return new UsersListResponse(serviceResponse.getBody());
    }
}
