/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.base.function;

/**
 * Predicate function.
 *
 * @param <T> object type
 */
public interface Predicate<T> {
	boolean test(T object);
}
