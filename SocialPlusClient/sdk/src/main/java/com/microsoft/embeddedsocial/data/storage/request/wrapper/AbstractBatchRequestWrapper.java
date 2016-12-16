/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage.request.wrapper;

import android.text.TextUtils;

import com.microsoft.embeddedsocial.server.model.FeedUserRequest;

/**
 * Base implementation of {@linkplain AbstractRequestWrapper} suitable for batch requests.
 * @param <Request>     network request type
 * @param <Response>    network response type
 */
public abstract class AbstractBatchRequestWrapper<Request extends FeedUserRequest, Response>
	extends AbstractRequestWrapper<Request, Response> {

	@Override
	protected boolean isFirstDataRequest(Request request) {
		return TextUtils.isEmpty(request.getCursor());
	}
}
