/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.base;

/**
 * Represents an object that should be disposed when it is no longer used.
 */
public interface IDisposable {

	/**
	 * Disposes the object.
	 */
	void dispose();
}
