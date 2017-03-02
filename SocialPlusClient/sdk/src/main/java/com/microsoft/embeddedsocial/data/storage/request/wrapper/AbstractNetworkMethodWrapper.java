/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage.request.wrapper;

import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.BaseRequest;

/**
 * An {@linkplain AbstractRequestWrapper} implementation that allows using method reference
 * instead of {@link #getNetworkResponse(BaseRequest)} abstract method.
 * @param <Request>     type of client request
 * @param <Response>    type of server response
 */
public abstract class AbstractNetworkMethodWrapper<Request extends BaseRequest, Response>
	extends AbstractRequestWrapper<Request, Response> {

	private final INetworkMethod<Request, Response> networkMethod;

	/**
	 * Base constructor.
	 * @param networkMethod     network method that should be called to obtain server response
	 */
	protected AbstractNetworkMethodWrapper(INetworkMethod<Request, Response> networkMethod) {
		this.networkMethod = networkMethod;
	}

	@Override
	protected final Response getNetworkResponse(Request request) throws NetworkRequestException {
		return networkMethod.getNetworkResponse(request);
	}

	/**
	 * Represents a network call that should be made to obtain response.
	 * @param <Request>     type of client request
	 * @param <Response>    type of server response
	 */
	public interface INetworkMethod<Request, Response> {

		/**
		 * Is called to obtain response from the server.
		 * @param   request   client request
		 * @return  server response
		 * @throws NetworkRequestException  if network call fails.
		 */
		Response getNetworkResponse(Request request) throws NetworkRequestException;
	}
}
