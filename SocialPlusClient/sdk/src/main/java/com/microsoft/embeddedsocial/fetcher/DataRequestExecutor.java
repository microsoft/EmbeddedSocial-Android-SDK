/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.fetcher;

import com.microsoft.embeddedsocial.base.function.Producer;
import com.microsoft.embeddedsocial.fetcher.base.DataState;
import com.microsoft.embeddedsocial.fetcher.base.RequestType;
import com.microsoft.embeddedsocial.server.ServerMethod;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.BaseRequest;
import com.microsoft.embeddedsocial.server.model.ListResponse;

import java.util.List;

/**
 * Executes a request to the server.
 *
 * @param <T> type of items in response
 * @param <R> type of request
 */
class DataRequestExecutor<T, R extends BaseRequest> {

	private final ServerMethod<? super R, ? extends ListResponse<T>> serverMethod;
	private final Producer<? extends R> requestProducer;

	DataRequestExecutor(ServerMethod<? super R, ? extends ListResponse<T>> serverMethod, Producer<? extends R> requestProducer) {
		this.requestProducer = requestProducer;
		this.serverMethod = serverMethod;
	}

	ListResponse<T> fetchRawResponse(DataState dataState, RequestType requestType, int pageSize) throws NetworkRequestException {
		R request = createRequest(dataState, requestType, pageSize);
		ListResponse<T> response = serverMethod.call(request);
		dataState.setContinuationKey(response.getContinuationKey());
		return response;
	}

	List<T> fetchData(DataState dataState, RequestType requestType, int pageSize) throws Exception {
		ListResponse<T> response = fetchRawResponse(dataState, requestType, pageSize);
		return response.getData();
	}

	protected R createRequest(DataState dataState, RequestType requestType, int pageSize) {
		R request = requestProducer.createNew();
		if (requestType == RequestType.SYNC_WITH_CACHE) {
			request.forceCacheUsage();
		}
		return request;
	}
}
