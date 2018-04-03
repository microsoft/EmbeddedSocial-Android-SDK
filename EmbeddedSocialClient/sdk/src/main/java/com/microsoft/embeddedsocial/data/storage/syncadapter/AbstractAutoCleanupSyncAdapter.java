/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage.syncadapter;

import com.j256.ormlite.dao.Dao;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.data.storage.transaction.DbTransaction;

import java.sql.SQLException;

/**
 * Base class for sync adapters that automatically deletes the data item after
 * successful synchronization.
 * @param <T>   sync item type
 */
public abstract class AbstractAutoCleanupSyncAdapter<T> extends AbstractSyncAdapter<T> {

	private final Dao<T, ?> itemDao;

	/**
	 * Creates an instance.
	 * @param item      the item to synchronize
	 * @param itemDao   the DAO corresponding to the item
	 */
	protected AbstractAutoCleanupSyncAdapter(T item, Dao<T, ?> itemDao) {
		super(item);
		this.itemDao = itemDao;
	}

	@Override
	protected void onSynchronizationSuccess(T item) {
		try {
			DbTransaction.performTransaction(itemDao, () -> itemDao.delete(item));
		} catch (SQLException e) {
			DebugLog.logException(e);
		}
	}
}
