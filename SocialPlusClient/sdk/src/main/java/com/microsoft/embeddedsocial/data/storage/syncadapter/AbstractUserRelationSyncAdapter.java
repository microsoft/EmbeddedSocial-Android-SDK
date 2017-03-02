/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage.syncadapter;

import com.microsoft.embeddedsocial.server.sync.exception.SynchronizationException;
import com.microsoft.embeddedsocial.data.storage.UserCache;
import com.microsoft.embeddedsocial.data.storage.model.UserRelationOperation;
import com.microsoft.embeddedsocial.server.exception.BadRequestException;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.relationship.UserRelationshipRequest;
import com.microsoft.embeddedsocial.server.sync.ISynchronizable;
import com.microsoft.embeddedsocial.server.sync.exception.OperationRejectedException;

/**
 * Base class for user relationship operation sync adapters.
 * @param <Response>    network response type
 */
public abstract class AbstractUserRelationSyncAdapter<Response> implements ISynchronizable {

	protected final UserRelationOperation operation;
	protected final UserCache userCache;
	protected Response response;

	protected AbstractUserRelationSyncAdapter(UserRelationOperation operation,
	                                          UserCache userCache) {

		this.operation = operation;
		this.userCache = userCache;
	}

	@Override
	public void synchronize() throws SynchronizationException {
		try {
			UserRelationshipRequest request = new UserRelationshipRequest(operation.getTargetUserHandle());
			response = performNetworkRequest(request);
		} catch (BadRequestException e) {
			throw new OperationRejectedException(e);
		} catch (NetworkRequestException e) {
			throw new SynchronizationException(e);
		}
	}

	/**
	 * Performs network request to sync the operation with the server.
	 * @param   request   request to execute
	 * @return  Response instance.
	 * @throws NetworkRequestException  if request execution fails
	 */
	protected abstract Response performNetworkRequest(UserRelationshipRequest request)
		throws NetworkRequestException;

	/**
	 * Is called when synchronization is completed and server response is obtained.
	 * @param response  server response
	 */
	protected abstract void onSynchronizationCompleted(Response response);

	@Override
	public void onSynchronizationSuccess() {
		onSynchronizationCompleted(response);
	}
}
