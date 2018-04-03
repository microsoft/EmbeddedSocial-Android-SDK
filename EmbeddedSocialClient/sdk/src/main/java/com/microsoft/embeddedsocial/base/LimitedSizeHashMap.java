/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.base;

import java.util.LinkedHashMap;

/**
 * Map with limited size.
 *
 * @param <K> key type
 * @param <V> value type
 */
public class LimitedSizeHashMap<K, V> extends LinkedHashMap<K, V> {

	private final int maxSize;

	public LimitedSizeHashMap(int maxSize) {
		super(maxSize);
		this.maxSize = maxSize;
	}

	@Override
	protected boolean removeEldestEntry(Entry<K, V> eldest) {
		return size() > maxSize;
	}
}
