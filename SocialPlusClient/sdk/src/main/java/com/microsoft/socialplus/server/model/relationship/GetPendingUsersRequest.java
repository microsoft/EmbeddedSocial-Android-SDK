package com.microsoft.socialplus.server.model.relationship;

import com.microsoft.socialplus.autorest.models.FeedResponseUserCompactView;
    import com.microsoft.rest.ServiceException;
    import com.microsoft.rest.ServiceResponse;
import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.microsoft.socialplus.server.model.FeedUserRequest;
        import com.microsoft.socialplus.server.model.UsersListResponse;

        import java.io.IOException;

public class GetPendingUsersRequest extends FeedUserRequest {

    @Override
    public UsersListResponse send() throws NetworkRequestException {
        ServiceResponse<FeedResponseUserCompactView> serviceResponse;
        try {
            serviceResponse =
                    PENDING.getPendingUsers(authorization, getCursor(), getBatchSize());
        } catch (ServiceException|IOException e) {
            throw new NetworkRequestException(e.getMessage());
        }
        checkResponseCode(serviceResponse);

        return new UsersListResponse(serviceResponse.getBody());
    }
}
