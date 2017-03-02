/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage.request.wrapper.account;

import com.microsoft.embeddedsocial.data.storage.request.wrapper.AbstractNetworkMethodWrapper;
import com.microsoft.embeddedsocial.server.model.account.GetMyProfileRequest;
import com.microsoft.embeddedsocial.server.model.account.GetUserProfileResponse;
import com.microsoft.embeddedsocial.data.storage.UserCache;

import java.sql.SQLException;

public class GetMyProfileWrapper extends AbstractNetworkMethodWrapper<GetMyProfileRequest, GetUserProfileResponse> {

    private final UserCache userCache;

    public GetMyProfileWrapper(INetworkMethod<GetMyProfileRequest, GetUserProfileResponse> networkMethod,
                                 UserCache userCache) {

        super(networkMethod);
        this.userCache = userCache;
    }

    @Override
    protected void storeResponse(GetMyProfileRequest request, GetUserProfileResponse response)
            throws SQLException {

        userCache.storeUserProfile(response.getUser());
    }

    @Override
    protected GetUserProfileResponse getCachedResponse(GetMyProfileRequest request)
            throws SQLException {

        return new GetUserProfileResponse(userCache.getUserProfile(request.getUserHandle()));
    }
}
