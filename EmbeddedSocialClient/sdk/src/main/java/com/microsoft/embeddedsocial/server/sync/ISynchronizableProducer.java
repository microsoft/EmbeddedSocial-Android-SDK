/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.sync;

import java.util.List;

/**
 * Produces synchronizable entities.
 */
public interface ISynchronizableProducer {

	/**
	 * Gets the list of synchronizable entities provided by this producer.
	 * @return  the list of entities
	 */
	List<ISynchronizable> getSynchronizableEntities();
}
