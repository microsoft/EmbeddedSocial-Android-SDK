/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage.syncadapter;

import com.j256.ormlite.dao.Dao;
import com.microsoft.embeddedsocial.data.storage.UserActionCache;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.content.topics.HideTopicRequest;
import com.microsoft.embeddedsocial.server.sync.exception.SynchronizationException;
import com.microsoft.embeddedsocial.server.IContentService;

/**
 * Sync adapter for hiding topics from feeds.
 */
public class HideTopicSyncAdapter extends AbstractAutoCleanupSyncAdapter<UserActionCache.HideTopicAction> {

	public HideTopicSyncAdapter(UserActionCache.HideTopicAction item,
	                               Dao<UserActionCache.HideTopicAction, ?> itemDao) {

		super(item, itemDao);
	}

	@Override
	protected void onSynchronize(UserActionCache.HideTopicAction item) throws NetworkRequestException,
			SynchronizationException {

		IContentService service = getServiceProvider().getContentService();
		service.hideTopic(new HideTopicRequest(item.getTopicHandle()));
	}
}
