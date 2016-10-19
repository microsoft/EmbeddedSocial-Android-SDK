/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.data.storage.syncadapter;

import com.microsoft.socialplus.base.GlobalObjectRegistry;
import com.microsoft.socialplus.data.storage.ActivityCache;
import com.microsoft.socialplus.server.SocialPlusServiceProvider;
import com.microsoft.socialplus.server.exception.BadRequestException;
import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.microsoft.socialplus.server.model.notification.UpdateNotificationStatusRequest;
import com.microsoft.socialplus.server.sync.ISynchronizable;
import com.microsoft.socialplus.server.sync.exception.OperationRejectedException;
import com.microsoft.socialplus.server.sync.exception.SynchronizationException;

/**
 * Sync adapter that notified the service of the last seen activity handle.
 */
public class LatestActivitySyncAdapter implements ISynchronizable {

	private final ActivityCache.MetadataStorage metadataStorage;

	public LatestActivitySyncAdapter(ActivityCache.MetadataStorage metadataStorage) {
		this.metadataStorage = metadataStorage;
	}

	@Override
	public void synchronize() throws SynchronizationException {
		String lastActivityHandle = metadataStorage.getLastActivityHandle();
		UpdateNotificationStatusRequest request = new UpdateNotificationStatusRequest(lastActivityHandle);
		try {
			GlobalObjectRegistry.getObject(SocialPlusServiceProvider.class)
				.getNotificationService()
				.updateNotificationStatus(request);
		} catch (BadRequestException e) {
			metadataStorage.clearLastActivityHandle();
			throw new OperationRejectedException(e);
		} catch (NetworkRequestException e) {
			throw new SynchronizationException(e);
		}
	}

	@Override
	public void onSynchronizationSuccess() {
		metadataStorage.markActivityHandleSynchronized();
	}
}
