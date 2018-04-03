/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.base.utils;

/**
 * Values bundles with read-only access.
 */
public interface ReadOnlyValues {

	<T> T getValue(String name);

	boolean getBooleanValue(String name);

}
