/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage.request.wrapper;

import android.text.TextUtils;

import com.microsoft.embeddedsocial.server.model.FeedUserRequest;

/**
 * An {@linkplain AbstractNetworkMethodWrapper} implementation for network methods that
 * return data in batches.
 * @param <Request>     type of client request
 * @param <Response>    type of server response
 */
public abstract class AbstractBatchNetworkMethodWrapper<Request extends FeedUserRequest, Response>
	extends AbstractNetworkMethodWrapper<Request, Response> {

	/**
	 * Base constructor.
	 * @param networkMethod     network method that should be called to obtain server response
	 */
	protected AbstractBatchNetworkMethodWrapper(INetworkMethod<Request, Response> networkMethod) {
		super(networkMethod);
	}

	@Override
	protected boolean isFirstDataRequest(Request request) {
		return TextUtils.isEmpty(request.getCursor());
	}
}
