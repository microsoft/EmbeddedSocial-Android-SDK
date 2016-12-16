/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage.request.wrapper.relationship;

import com.microsoft.embeddedsocial.server.model.UsersListResponse;
import com.microsoft.embeddedsocial.server.model.relationship.GetBlockedUsersRequest;
import com.microsoft.embeddedsocial.data.storage.UserCache;
import com.microsoft.embeddedsocial.data.storage.request.wrapper.AbstractBatchNetworkMethodWrapper;

import java.sql.SQLException;

public class BlockedUserFeedRequestWrapper
        extends AbstractBatchNetworkMethodWrapper<GetBlockedUsersRequest, UsersListResponse> {

    protected final UserCache userCache;
    protected final UserCache.UserFeedType feedType;

    public BlockedUserFeedRequestWrapper(
            INetworkMethod<GetBlockedUsersRequest, UsersListResponse> networkMethod,
            UserCache userCache,
            UserCache.UserFeedType feedType) {

        super(networkMethod);
        this.userCache = userCache;
        this.feedType = feedType;
    }

    @Override
    protected void storeResponse(GetBlockedUsersRequest batchUserRequest,
                                 UsersListResponse usersListResponse) throws SQLException {

        userCache.storeUserFeed(batchUserRequest, feedType, usersListResponse);
    }

    @Override
    protected UsersListResponse getCachedResponse(GetBlockedUsersRequest batchUserRequest)
            throws SQLException {

        return userCache.getResponse(feedType);
    }
}
