/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage.syncadapter;

import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.sync.exception.SynchronizationException;
import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.server.EmbeddedSocialServiceProvider;
import com.microsoft.embeddedsocial.server.exception.BadRequestException;
import com.microsoft.embeddedsocial.server.sync.ISynchronizable;
import com.microsoft.embeddedsocial.server.sync.exception.OperationRejectedException;

/**
 * Base class for synchronization adapters.
 * @param <T>   sync item type
 */
public abstract class AbstractSyncAdapter<T> implements ISynchronizable {

	private final T item;
	private final EmbeddedSocialServiceProvider serviceProvider;

	/**
	 * Creates an instance.
	 * @param item  the item to synchronize
	 */
	protected AbstractSyncAdapter(T item) {
		this.item = item;
		this.serviceProvider = GlobalObjectRegistry.getObject(EmbeddedSocialServiceProvider.class);
	}

	/**
	 * Gets the item for synchronization.
	 * @return  sync item.
	 */
	protected final T getItem() {
		return item;
	}

	/**
	 * Gets Embedded Social API service provider.
	 * @return  {@link EmbeddedSocialServiceProvider} instance.
	 */
	protected final EmbeddedSocialServiceProvider getServiceProvider() {
		return serviceProvider;
	}

	/**
	 * Is called when the item should be synchronized.
	 * @param item  the item to synchronize
	 * @throws NetworkRequestException  if network request fails
	 * @throws SynchronizationException if synchronization fails
	 */
	protected abstract void onSynchronize(T item)
		throws NetworkRequestException, SynchronizationException;

	/**
	 * Is called when synchronization is completed successfully.
	 * @param item  the item that was synchronized.
	 */
	protected abstract void onSynchronizationSuccess(T item);

	@Override
	public final void synchronize() throws SynchronizationException {
		try {
			onSynchronize(item);
		} catch (BadRequestException e) {
			throw new OperationRejectedException(e);
		} catch (NetworkRequestException e) {
			throw new SynchronizationException(e);
		}
	}

	@Override
	public final void onSynchronizationSuccess() {
		onSynchronizationSuccess(item);
	}
}
