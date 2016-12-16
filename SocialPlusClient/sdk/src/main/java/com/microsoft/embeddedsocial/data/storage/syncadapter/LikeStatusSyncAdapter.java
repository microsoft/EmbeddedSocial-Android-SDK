/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage.syncadapter;

import com.j256.ormlite.dao.Dao;
import com.microsoft.embeddedsocial.data.storage.UserActionCache;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.like.AddLikeRequest;
import com.microsoft.embeddedsocial.server.model.like.RemoveLikeRequest;
import com.microsoft.embeddedsocial.server.sync.exception.SynchronizationException;
import com.microsoft.embeddedsocial.server.IContentService;

/**
 * Sync adapter for like statuses.
 */
public class LikeStatusSyncAdapter extends AbstractAutoCleanupSyncAdapter<UserActionCache.LikeChangedAction> {

	public LikeStatusSyncAdapter(Dao<UserActionCache.LikeChangedAction, Integer> likeDao,
	                             UserActionCache.LikeChangedAction likeAction) {

		super(likeAction, likeDao);
	}

	@Override
	protected void onSynchronize(UserActionCache.LikeChangedAction item)
		throws NetworkRequestException, SynchronizationException {

		IContentService contentService = getServiceProvider().getContentService();

		if (item.getStatus()) {
			contentService.addLike(new AddLikeRequest(item.getContentHandle(), item.getContentType()));
		} else {
			contentService.removeLike(new RemoveLikeRequest(item.getContentHandle(), item.getContentType()));
		}
	}
}
