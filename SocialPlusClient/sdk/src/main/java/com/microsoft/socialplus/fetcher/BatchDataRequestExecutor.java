/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.fetcher;

import com.microsoft.socialplus.base.function.Producer;
import com.microsoft.socialplus.fetcher.base.DataState;
import com.microsoft.socialplus.fetcher.base.RequestType;
import com.microsoft.socialplus.server.ServerMethod;
import com.microsoft.socialplus.server.model.FeedUserRequest;
import com.microsoft.socialplus.server.model.ListResponse;

/**
 * Executes a batch data request to the server (i.e. loads data by pages).
 *
 * @param <T> type of items in response
 * @param <R> type of request
 */
class BatchDataRequestExecutor<T, R extends FeedUserRequest> extends DataRequestExecutor<T, R> {

	public BatchDataRequestExecutor(ServerMethod<R, ? extends ListResponse<T>> serverMethod, Producer<R> requestProducer) {
		super(serverMethod, requestProducer);
	}

	@Override
	protected R createRequest(DataState dataState, RequestType requestType, int pageSize) {
		R request = super.createRequest(dataState, requestType, pageSize);
		request.setBatchSize(pageSize);
		request.setCursor(dataState.getContinuationKey());
		return request;
	}
}
