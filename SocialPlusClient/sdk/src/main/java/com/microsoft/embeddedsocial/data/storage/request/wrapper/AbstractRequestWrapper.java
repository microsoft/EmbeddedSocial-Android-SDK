/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage.request.wrapper;

import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.BaseRequest;

import java.sql.SQLException;

/**
 * Contains general logic for content caching.
 * @param <Request>     network request type
 * @param <Response>    network response type
 */
public abstract class AbstractRequestWrapper<Request extends BaseRequest, Response> {

	/**
	 * Gets response from the network.
	 * @param   request   data request
	 * @return  response from the server.
	 * @throws  NetworkRequestException     if network request fails
	 */
	protected abstract Response getNetworkResponse(Request request) throws NetworkRequestException;

	/**
	 * Stores a response obtained from the server in the cache.
	 * @param request   data request
	 * @param response  response from the server
	 */
	protected abstract void storeResponse(Request request, Response response) throws SQLException;

	/**
	 * Gets response from the cache.
	 * @param   request   data request
	 * @return  cached response.
	 * @throws  SQLException    if cache fails
	 */
	protected abstract Response getCachedResponse(Request request) throws SQLException;

	/**
	 * Checks if given request is the first request for data (isn't a continuation request).
	 * @param   request   network request
	 * @return  true if the request is the first request for data.
	 */
	protected boolean isFirstDataRequest(Request request) {
		return true;
	}

	/**
	 * Gets a response corresponding to the specified request. Tries to obtain the response
	 * from the network first. If that attempt fails, switches to cache and tries to obtain
	 * the response from cache.
	 * @param   request   data request
	 * @return  response of specified type
	 * @throws  NetworkRequestException if response can't be obtained neither from network nor from cache.
	 */
	public Response getResponse(Request request) throws NetworkRequestException {
		Response response;
		boolean cachedResponse = false;

		if (request.isCacheOnly()) {
			try {
				response = getCachedResponse(request);
				cachedResponse = true;
			} catch (SQLException e) {
				DebugLog.logException(e);
				throw new NetworkRequestException(e);
			}
		} else {
			try {
				response = request.send();
				onNetworkResponseReceived(request, response);
				storeResponseSafely(request, response);
			} catch (NetworkRequestException e) {
				DebugLog.logException(e);
				if (!isFirstDataRequest(request)) {
					throw e;
				}
				try {
					response = getCachedResponse(request);
					cachedResponse = true;
				} catch (SQLException e2) {
					DebugLog.logException(e2);
					// throw the original exception
					throw e;
				}
			}
		}

		onResponseIsReady(request, response, cachedResponse);

		return response;
	}

	/**
	 * Is called immediately after a response is obtained from the server, before it's saved to cache.
	 * <br/> Does nothing in default implementation.
	 * @param request   client request
	 * @param response  server response
	 */
	@SuppressWarnings("unused")
	protected void onNetworkResponseReceived(Request request, Response response) {  }

	/**
	 * Is called when a response is ready to be returned to the caller.
	 * <br/> Does nothing in default implementation.
	 * @param request               client request
	 * @param response              response (either cached or from the server)
	 * @param cachedResponseUsed    true if response was loaded from cache
	 */
	@SuppressWarnings("unused")
	protected void onResponseIsReady(Request request, Response response, boolean cachedResponseUsed) {  }

	private void storeResponseSafely(Request request, Response response) {
		try {
			storeResponse(request, response);
		} catch (Exception e) {
			DebugLog.logException(e);
		}
	}
}
