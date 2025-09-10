/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.fetcher;

import com.microsoft.embeddedsocial.autorest.models.IdentityProvider;
import com.microsoft.embeddedsocial.fetcher.base.DataState;
import com.microsoft.embeddedsocial.fetcher.base.Fetcher;
import com.microsoft.embeddedsocial.fetcher.base.RequestType;
import com.microsoft.embeddedsocial.server.ISearchService;
import com.microsoft.embeddedsocial.server.model.discover.FindUsersWithThirdPartyAccountsRequest;
import com.microsoft.embeddedsocial.server.model.view.UserCompactView;
import com.microsoft.embeddedsocial.social.AuthorizationRequest;
import com.microsoft.embeddedsocial.social.FriendlistLoader;
import com.microsoft.embeddedsocial.social.FriendlistLoaders;

import java.util.Collections;
import java.util.List;

/**
 * Fetches friends list from social networks.
 */
final class FriendlistFetcher extends Fetcher<UserCompactView> {

    private final DataRequestExecutor<UserCompactView, ?> requestExecutor;
    private final FriendlistLoader friendlistLoader;
    private final AuthorizationRequest authorizationRequest;

    private List<String> thirdPartyFriendIds;

    FriendlistFetcher(ISearchService server, IdentityProvider identityProvider, AuthorizationRequest authorizationRequest) {
        this.authorizationRequest = authorizationRequest;
        friendlistLoader = FriendlistLoaders.newFriendlistLoader(identityProvider);
        requestExecutor = new DataRequestExecutor<>(
            server::findUsersWithThirdPartyAccounts,
            () -> new FindUsersWithThirdPartyAccountsRequest(identityProvider, thirdPartyFriendIds)
        );
    }

    @Override
    protected List<UserCompactView> fetchDataPage(DataState dataState, RequestType requestType, int pageSize) throws Exception {
        if (requestType == RequestType.FORCE_REFRESH
            || (requestType == RequestType.REGULAR && thirdPartyFriendIds == null)) {

            if (!friendlistLoader.isAuthorizedToSocialNetwork()) {
                // a fragment listens to it and launches an authorization
                // it won't return until the user is authorized
                // if authorization failed or was cancelled an exception is thrown
                authorizationRequest.call();
            }
            thirdPartyFriendIds = friendlistLoader.getThirdPartyFriendIds();
        }
        if (thirdPartyFriendIds == null || thirdPartyFriendIds.isEmpty()) {
            return Collections.emptyList();
        }
        return requestExecutor.fetchData(dataState, requestType, pageSize);
    }

}
