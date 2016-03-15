/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.data.storage;

import com.microsoft.socialplus.data.storage.request.wrapper.AbstractRequestWrapper;
import com.microsoft.socialplus.server.ISearchService;
import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.microsoft.socialplus.server.model.FeedUserRequest;
import com.microsoft.socialplus.server.model.UserRequest;
import com.microsoft.socialplus.server.model.UsersListResponse;
import com.microsoft.socialplus.server.model.content.topics.TopicsListResponse;
import com.microsoft.socialplus.server.model.discover.FindUsersWithThirdPartyAccountsRequest;
import com.microsoft.socialplus.server.model.search.GetTrendingHashtagsResponse;
import com.microsoft.socialplus.server.model.search.SearchRequest;
import com.microsoft.socialplus.server.model.search.SearchTopicsAutocompleteResponse;

import java.sql.SQLException;

/**
 * Provides transparent cache functionality on top of {@linkplain ISearchService}.
 */
public class SearchServiceCachingWrapper implements ISearchService {

	private final TrendingHashtagsRequestWrapper hashtagsRequestWrapper = new TrendingHashtagsRequestWrapper();
	private final SearchHistory searchHistory = new SearchHistory();
	private final ISearchService wrappedService;

	/**
	 * Creates an instance.
	 *
	 * @param wrappedService the service to wrap
	 */
	public SearchServiceCachingWrapper(ISearchService wrappedService) {
		this.wrappedService = wrappedService;
	}

	@Override
	public UsersListResponse findUsersWithThirdPartyAccounts(FindUsersWithThirdPartyAccountsRequest request)
		throws NetworkRequestException {

		return wrappedService.findUsersWithThirdPartyAccounts(request);
	}

	@Override
	public GetTrendingHashtagsResponse getTrendingHashtags(UserRequest request)
		throws NetworkRequestException {

		return hashtagsRequestWrapper.getResponse(request);
	}

	@Override
	public TopicsListResponse searchTopics(SearchRequest request)
		throws NetworkRequestException {

		return wrappedService.searchTopics(request);
	}

	@Override
	public SearchTopicsAutocompleteResponse searchTopicsAutocomplete(SearchRequest request)
		throws NetworkRequestException {

		return wrappedService.searchTopicsAutocomplete(request);
	}

	@Override
	public UsersListResponse searchUsers(SearchRequest request) throws NetworkRequestException {
		return wrappedService.searchUsers(request);
	}

	@Override
	public UsersListResponse getPopularUsers(FeedUserRequest request) throws NetworkRequestException {
		return wrappedService.getPopularUsers(request);
	}

	private class TrendingHashtagsRequestWrapper extends AbstractRequestWrapper<UserRequest, GetTrendingHashtagsResponse> {

		@Override
		protected GetTrendingHashtagsResponse getNetworkResponse(UserRequest request)
			throws NetworkRequestException {

			return wrappedService.getTrendingHashtags(request);
		}

		@Override
		protected void storeResponse(UserRequest request, GetTrendingHashtagsResponse response)
			throws SQLException {

			searchHistory.storeTrendingHashtags(response.getData());
		}

		@Override
		protected GetTrendingHashtagsResponse getCachedResponse(UserRequest request)
			throws SQLException {

			return new GetTrendingHashtagsResponse(searchHistory.getTrendingHashtags());
		}
	}
}
