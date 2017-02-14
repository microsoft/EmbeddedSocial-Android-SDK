/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.sync;

import com.microsoft.embeddedsocial.server.sync.exception.SynchronizationException;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.server.sync.exception.OperationRejectedException;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Uploads all syncable data to the server.
 */
public class DataSynchronizer {

	private final Set<SyncProducer> syncProducers = new LinkedHashSet<>();

	/**
	 * Registers a sync producer with this synchronizer.
	 * @param   producer    sync producer
	 * @param   name        producer name (used mostly for logging)
	 */
	public void registerSyncProducer(ISynchronizableProducer producer, String name) {
		syncProducers.add(new SyncProducer(producer, name));
	}

	/**
	 * Uploads all available data to the server.
	 * @return  true if synchronization finishes successfully.
	 */
	public boolean synchronize() {
		boolean result = true;

		for (SyncProducer producerInfo : syncProducers) {
			ISynchronizableProducer producer = producerInfo.producer;
			try {
				int syncedEntities = synchronizeProducer(producer, producerInfo.producerName);
				if (syncedEntities > 0) {
					DebugLog.i("synced " + syncedEntities + " items from '"
						+ producerInfo.producerName + "'");
				}
			} catch (SynchronizationException e) {
				DebugLog.e("error in sync producer '" + producerInfo.producerName + "'");
				DebugLog.logException(e);
				result = false;
				// we continue synchronization on failure to give a chance to other
				// sync producers (in case some server features failed, but other
				// still work)
			}
		}

		return result;
	}

	private int synchronizeProducer(ISynchronizableProducer producer, String producerName)
		throws SynchronizationException {

		List<ISynchronizable> entities = producer.getSynchronizableEntities();
		int syncedEntities = 0;
		// Store the first exception from this producer
		SynchronizationException exception = null;

		for (ISynchronizable entity : entities) {
			try {
				synchronizeEntity(entity, producerName);
				++syncedEntities;
			} catch (SynchronizationException e) {
				if (exception == null) {
					exception = e;
				}
			} catch (Exception e) {
				if (exception == null) {
					exception = new SynchronizationException("Synchronization failed: " + e.getMessage(), e);
				}
			}
		}

		if (exception != null) {
			throw exception;
		}

		return syncedEntities;
	}

	private void synchronizeEntity(ISynchronizable entity, String producerName)
		throws SynchronizationException {

		try {
			entity.synchronize();
		} catch (OperationRejectedException e) {
			DebugLog.e(producerName + ": server rejected sync request "
				+ entity + " as invalid");
			// do nothing here - rejected data is invalid and will be removed
			// automatically
		}
		entity.onSynchronizationSuccess();
	}

	/**
	 * Stores a sync producer along with its name.
	 */
	private static class SyncProducer {

		private final ISynchronizableProducer producer;
		private final String producerName;

		SyncProducer(ISynchronizableProducer producer, String producerName) {
			this.producer = producer;
			this.producerName = producerName;
		}

		@Override
		public boolean equals(Object o) {
			return producer.equals(o);
		}

		@Override
		public int hashCode() {
			return producer.hashCode();
		}
	}
}
