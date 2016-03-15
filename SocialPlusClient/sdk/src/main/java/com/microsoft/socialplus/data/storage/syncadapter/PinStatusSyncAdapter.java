/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.data.storage.syncadapter;

import com.j256.ormlite.dao.Dao;
import com.microsoft.socialplus.data.storage.UserActionCache;
import com.microsoft.socialplus.server.IContentService;
import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.microsoft.socialplus.server.model.pin.AddPinRequest;
import com.microsoft.socialplus.server.model.pin.RemovePinRequest;
import com.microsoft.socialplus.server.sync.exception.SynchronizationException;

/**
 * Sync adapter for pin statuses.
 */
public class PinStatusSyncAdapter extends AbstractAutoCleanupSyncAdapter<UserActionCache.PinChangedAction> {

	public PinStatusSyncAdapter(Dao<UserActionCache.PinChangedAction, Integer> pinDao,
	                            UserActionCache.PinChangedAction pinAction) {

		super(pinAction, pinDao);
	}

	@Override
	protected void onSynchronize(UserActionCache.PinChangedAction item)
		throws NetworkRequestException, SynchronizationException {

		IContentService contentService = getServiceProvider().getContentService();

		if (item.getStatus()) {
			contentService.addPin(new AddPinRequest(item.getTopicHandle()));
		} else {
			contentService.removePin(new RemovePinRequest(item.getTopicHandle()));
		}
	}
}
