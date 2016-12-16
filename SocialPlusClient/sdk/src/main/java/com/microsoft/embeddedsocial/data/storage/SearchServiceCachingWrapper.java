/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage;

import com.microsoft.embeddedsocial.data.storage.request.wrapper.AbstractRequestWrapper;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.UsersListResponse;
import com.microsoft.embeddedsocial.server.model.content.topics.TopicsListResponse;
import com.microsoft.embeddedsocial.server.ISearchService;
import com.microsoft.embeddedsocial.server.model.discover.FindUsersWithThirdPartyAccountsRequest;
import com.microsoft.embeddedsocial.server.model.search.GetAutocompletedHashtagsRequest;
import com.microsoft.embeddedsocial.server.model.search.GetPopularUsersRequest;
import com.microsoft.embeddedsocial.server.model.search.GetTrendingHashtagsRequest;
import com.microsoft.embeddedsocial.server.model.search.GetTrendingHashtagsResponse;
import com.microsoft.embeddedsocial.server.model.search.AutocompletedHashtagsResponse;
import com.microsoft.embeddedsocial.server.model.search.SearchTopicsRequest;
import com.microsoft.embeddedsocial.server.model.search.SearchUsersRequest;

import java.sql.SQLException;

/**
 * Provides transparent cache functionality on top of {@linkplain ISearchService}.
 */
public class SearchServiceCachingWrapper implements ISearchService {

	private final TrendingHashtagsRequestWrapper hashtagsRequestWrapper = new TrendingHashtagsRequestWrapper();
	private final SearchHistory searchHistory = new SearchHistory();

	@Override
	public UsersListResponse findUsersWithThirdPartyAccounts(FindUsersWithThirdPartyAccountsRequest request)
		throws NetworkRequestException {

		return request.send();
	}

	@Override
	public GetTrendingHashtagsResponse getTrendingHashtags(GetTrendingHashtagsRequest request)
		throws NetworkRequestException {

		return hashtagsRequestWrapper.getResponse(request);
	}

	@Override
	public TopicsListResponse searchTopics(SearchTopicsRequest request)
		throws NetworkRequestException {

		return request.send();
	}

	@Override
	public AutocompletedHashtagsResponse searchHashtagsAutocomplete(GetAutocompletedHashtagsRequest request)
		throws NetworkRequestException {

		return request.send();
	}

	@Override
	public UsersListResponse searchUsers(SearchUsersRequest request) throws NetworkRequestException {
		return request.send();
	}

	@Override
	public UsersListResponse getPopularUsers(GetPopularUsersRequest request) throws NetworkRequestException {
		return request.send();
	}

	private class TrendingHashtagsRequestWrapper extends
			AbstractRequestWrapper<GetTrendingHashtagsRequest, GetTrendingHashtagsResponse> {

		@Override
		protected GetTrendingHashtagsResponse getNetworkResponse(GetTrendingHashtagsRequest request)
			throws NetworkRequestException {

			return request.send();
		}

		@Override
		protected void storeResponse(GetTrendingHashtagsRequest request, GetTrendingHashtagsResponse response)
			throws SQLException {

			searchHistory.storeTrendingHashtags(response.getData());
		}

		@Override
		protected GetTrendingHashtagsResponse getCachedResponse(GetTrendingHashtagsRequest request)
			throws SQLException {

			return new GetTrendingHashtagsResponse(searchHistory.getTrendingHashtags());
		}
	}
}
