/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.sync;

import com.microsoft.embeddedsocial.server.sync.exception.SynchronizationException;

/**
 * Represents a synchronizable (uploadable) entity that can be synced with the server.
 */
public interface ISynchronizable {

	/**
	 * Uploads entity contents to the server.
	 * @throws SynchronizationException if synchronization fails
	 */
	void synchronize() throws SynchronizationException;

	/**
	 * Is called if synchronization is performed successfully.
	 */
	void onSynchronizationSuccess();
}
