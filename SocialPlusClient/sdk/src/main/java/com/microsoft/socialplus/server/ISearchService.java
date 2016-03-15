/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server;

import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.microsoft.socialplus.server.model.FeedUserRequest;
import com.microsoft.socialplus.server.model.UserRequest;
import com.microsoft.socialplus.server.model.UsersListResponse;
import com.microsoft.socialplus.server.model.content.topics.TopicsListResponse;
import com.microsoft.socialplus.server.model.discover.FindUsersWithThirdPartyAccountsRequest;
import com.microsoft.socialplus.server.model.search.GetTrendingHashtagsResponse;
import com.microsoft.socialplus.server.model.search.SearchRequest;
import com.microsoft.socialplus.server.model.search.SearchTopicsAutocompleteResponse;

/**
 * Interface allows to search for  users/content
 */
public interface ISearchService {

	UsersListResponse findUsersWithThirdPartyAccounts(FindUsersWithThirdPartyAccountsRequest request) throws NetworkRequestException;

	GetTrendingHashtagsResponse getTrendingHashtags(UserRequest request) throws NetworkRequestException;

	TopicsListResponse searchTopics(SearchRequest request) throws NetworkRequestException;

	SearchTopicsAutocompleteResponse searchTopicsAutocomplete(SearchRequest request) throws NetworkRequestException;

	UsersListResponse searchUsers(SearchRequest request) throws NetworkRequestException;

	UsersListResponse getPopularUsers(FeedUserRequest request) throws NetworkRequestException;
}
