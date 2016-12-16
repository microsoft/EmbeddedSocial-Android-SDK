/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage.syncadapter;

import com.j256.ormlite.dao.Dao;
import com.microsoft.embeddedsocial.server.model.content.replies.RemoveReplyRequest;
import com.microsoft.embeddedsocial.server.sync.exception.SynchronizationException;
import com.microsoft.embeddedsocial.data.storage.UserActionCache;
import com.microsoft.embeddedsocial.server.IContentService;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.content.comments.RemoveCommentRequest;
import com.microsoft.embeddedsocial.server.model.content.topics.RemoveTopicRequest;

/**
 * Contains base functionality related to removing content from the server.
 */
public class RemoveContentActionSyncAdapter extends AbstractAutoCleanupSyncAdapter<UserActionCache.ContentRemovedAction> {

	private final INetworkOperation operation;

	private RemoveContentActionSyncAdapter(
		Dao<UserActionCache.ContentRemovedAction, Integer> removeActionDao,
		UserActionCache.ContentRemovedAction action, INetworkOperation operation) {

		super(action, removeActionDao);
		this.operation = operation;
	}

	@Override
	protected void onSynchronize(UserActionCache.ContentRemovedAction item)
		throws NetworkRequestException, SynchronizationException {

		operation.performNetworkOperation(getServiceProvider().getContentService());
	}

	/**
	 * Creates appropriate adapter for the specified action.
	 * @param removeActionDao   content removal action DAO
	 * @param action            removal action
	 * @return  {@linkplain RemoveContentActionSyncAdapter} instance.
	 */
	public static RemoveContentActionSyncAdapter createAdapter(
		Dao<UserActionCache.ContentRemovedAction, Integer> removeActionDao,
		UserActionCache.ContentRemovedAction action) {

		INetworkOperation operation;

		switch (action.getContentType()) {
			case COMMENT:
				operation = (service) -> service.removeComment(new RemoveCommentRequest(action.getContentHandle()));
				break;

			case REPLY:
				operation = (service) -> service.removeReply(new RemoveReplyRequest(action.getContentHandle()));
				break;

			case TOPIC:
			default:
				operation = (service) -> service.removeTopic(new RemoveTopicRequest(action.getContentHandle()));
		}

		return new RemoveContentActionSyncAdapter(removeActionDao, action, operation);
	}

	private interface INetworkOperation {
		void performNetworkOperation(IContentService service)
			throws SynchronizationException, NetworkRequestException;
	}
}
