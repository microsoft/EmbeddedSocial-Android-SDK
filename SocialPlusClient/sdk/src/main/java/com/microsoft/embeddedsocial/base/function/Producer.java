/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.base.function;

/**
 * Produces new objects of some type
 *
 * @param <T> object type
 */
public interface Producer<T> {
	T createNew();
}
