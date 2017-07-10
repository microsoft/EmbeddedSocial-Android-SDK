/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.base;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registers global objects in memory and allows for their easy retrieval
 */
public final class GlobalObjectRegistry {

	private static final Map<Class<?>, Object> OBJECTS_MAP = new ConcurrentHashMap<>();

	private GlobalObjectRegistry() {
	}

	/**
	 * Saves global object to the registry
	 *
	 * @param cls    object class
	 * @param object object itself
	 */
	public static <T> void addObject(Class<T> cls, T object) {
		OBJECTS_MAP.put(cls, object);
	}

	/**
	 * Saves global object to the registry
	 *
	 * @param object object itself
	 */
	public static void addObject(Object object) {
		OBJECTS_MAP.put(object.getClass(), object);
	}

	/**
	 * Retrieves saved object from registry
	 *
	 * @param cls object class
	 * @return saved object or <code>null</code>
	 */
	public static <T> T getObject(Class<T> cls) {
		return cls.cast(OBJECTS_MAP.get(cls));
	}

}
