/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.server;

import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.microsoft.socialplus.server.model.UsersListResponse;
import com.microsoft.socialplus.server.model.content.topics.TopicsListResponse;
import com.microsoft.socialplus.server.model.discover.FindUsersWithThirdPartyAccountsRequest;
import com.microsoft.socialplus.server.model.search.GetAutocompletedHashtagsRequest;
import com.microsoft.socialplus.server.model.search.GetPopularUsersRequest;
import com.microsoft.socialplus.server.model.search.GetTrendingHashtagsRequest;
import com.microsoft.socialplus.server.model.search.GetTrendingHashtagsResponse;
import com.microsoft.socialplus.server.model.search.AutocompletedHashtagsResponse;
import com.microsoft.socialplus.server.model.search.SearchTopicsRequest;
import com.microsoft.socialplus.server.model.search.SearchUsersRequest;

/**
 * Interface allows to search for  users/content
 */
public interface ISearchService {

	UsersListResponse findUsersWithThirdPartyAccounts(FindUsersWithThirdPartyAccountsRequest request) throws NetworkRequestException;

	GetTrendingHashtagsResponse getTrendingHashtags(GetTrendingHashtagsRequest request) throws NetworkRequestException;

	TopicsListResponse searchTopics(SearchTopicsRequest request) throws NetworkRequestException;

	AutocompletedHashtagsResponse searchHashtagsAutocomplete(GetAutocompletedHashtagsRequest request) throws NetworkRequestException;

	UsersListResponse searchUsers(SearchUsersRequest request) throws NetworkRequestException;

	UsersListResponse getPopularUsers(GetPopularUsersRequest request) throws NetworkRequestException;
}
