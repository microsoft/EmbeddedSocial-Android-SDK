/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model;

/**
 * Represents an object that has a timestamp.
 */
public interface TimedItem {

	/**
	 * Gets time elapsed since entity creation in seconds.
	 * @return  elapsed time.
	 */
	long getElapsedSeconds();
}
