/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.data.storage.syncadapter;

import com.j256.ormlite.dao.Dao;
import com.microsoft.socialplus.data.storage.model.EditedTopic;
import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.microsoft.socialplus.server.model.content.topics.UpdateTopicRequest;
import com.microsoft.socialplus.server.sync.exception.SynchronizationException;

/**
 * Sync adapter for edited topics.
 */
public class EditedTopicSyncAdapter extends AbstractAutoCleanupSyncAdapter<EditedTopic> {

	public EditedTopicSyncAdapter(EditedTopic item, Dao<EditedTopic, ?> itemDao) {
		super(item, itemDao);
	}

	@Override
	protected void onSynchronize(EditedTopic item) throws NetworkRequestException,
		SynchronizationException {

		UpdateTopicRequest request = new UpdateTopicRequest(
			item.getTopicHandle(),
			item.getTopicTitle(),
			item.getTopicText(),
			item.getTopicCategories()
		);
		getServiceProvider().getContentService().updateTopic(request);
	}
}
