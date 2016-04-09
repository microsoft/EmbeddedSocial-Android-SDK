/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.data.storage.request.wrapper.relationship;

import com.microsoft.socialplus.data.storage.UserCache;
import com.microsoft.socialplus.data.storage.request.wrapper.AbstractBatchNetworkMethodWrapper;
import com.microsoft.socialplus.server.model.UsersListResponse;
import com.microsoft.socialplus.server.model.relationship.GetMyFollowingFeedRequest;

import java.sql.SQLException;

public class MyFollowingFeedRequestWrapper
        extends AbstractBatchNetworkMethodWrapper<GetMyFollowingFeedRequest, UsersListResponse> {

    private final UserCache userCache;
    private final UserCache.UserFeedType feedType;

    public MyFollowingFeedRequestWrapper(
            INetworkMethod<GetMyFollowingFeedRequest, UsersListResponse> networkMethod,
            UserCache userCache, UserCache.UserFeedType feedType) {

        super(networkMethod);
        this.userCache = userCache;
        this.feedType = feedType;
    }

    @Override
    protected void storeResponse(GetMyFollowingFeedRequest request,
                                 UsersListResponse response) throws SQLException {

        userCache.storeUserFeed(request, feedType, response);
    }

    @Override
    protected UsersListResponse getCachedResponse(GetMyFollowingFeedRequest request)
            throws SQLException {

        return userCache.getResponse(feedType);
    }
}
