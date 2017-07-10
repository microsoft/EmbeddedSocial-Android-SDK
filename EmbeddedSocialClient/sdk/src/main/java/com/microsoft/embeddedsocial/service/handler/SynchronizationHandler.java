/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.service.handler;

import android.content.Context;
import android.content.Intent;

import com.microsoft.embeddedsocial.base.service.IServiceIntentHandler;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.data.storage.ActivityCache;
import com.microsoft.embeddedsocial.data.storage.PostStorage;
import com.microsoft.embeddedsocial.data.storage.UserActionCache;
import com.microsoft.embeddedsocial.gcm.GcmTokenHolder;
import com.microsoft.embeddedsocial.server.sync.DataSynchronizer;
import com.microsoft.embeddedsocial.data.storage.UserCache;
import com.microsoft.embeddedsocial.service.ServiceAction;

/**
 * Uploads all available data to the server.
 */
public class SynchronizationHandler implements IServiceIntentHandler<ServiceAction> {

	public static final String PENDING_POST_SYNC_NAME = "posts";

	private final DataSynchronizer synchronizer = new DataSynchronizer();

	/**
	 * Creates an instance.
	 */
	public SynchronizationHandler(Context context) {
		UserActionCache userActionCache = new UserActionCache();
		PostStorage postStorage = new PostStorage(context);
		synchronizer.registerSyncProducer(postStorage::getPendingPosts, PENDING_POST_SYNC_NAME);
		synchronizer.registerSyncProducer(postStorage::getPendingDiscussionItems, "comments/replies");
		synchronizer.registerSyncProducer(postStorage::getPendingEditedTopics, "topic edits");
		synchronizer.registerSyncProducer(userActionCache::getPendingLikeActions, "likes");
		synchronizer.registerSyncProducer(userActionCache::getPendingPinActions, "pins");
		synchronizer.registerSyncProducer(userActionCache::getPendingHideTopicActions, "hidden topics");
		synchronizer.registerSyncProducer(userActionCache::getPendingReportContentActions,
			"reported content");
		synchronizer.registerSyncProducer(new ActivityCache(context)::getActivityHandleSyncActions,
			"notification updates");
		synchronizer.registerSyncProducer(new UserCache()::getPendingUserRelationOperations,
			"user relations");
		synchronizer.registerSyncProducer(userActionCache::getPendingContentRemovalActions,
			"removals");
		synchronizer.registerSyncProducer(GcmTokenHolder.create(context)::getTokenSyncOperations,
			"gcm");
	}

	@Override
	public void handleIntent(ServiceAction action, Intent intent) {
		// currently this should be synchronized in app scope
		synchronized (SynchronizationHandler.class) {
			boolean synced = synchronizer.synchronize();
			DebugLog.i(synced ? "sync succeeded" : "sync failed");
		}
	}

	@Override
	public void dispose() {  }
}
