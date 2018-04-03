/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.base.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Values bundle.
 */
public class Values implements ReadOnlyValues {

	private final Map<String, Object> values = new HashMap<>();

	public void setValue(String name, Object value) {
		values.put(name, value);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getValue(String name) {
		return (T) values.get(name);
	}

	@Override
	public boolean getBooleanValue(String name) {
		Boolean value = getValue(name);
		return value == Boolean.TRUE;
	}

	// TODO: add get methods for other simple types if needed

	public static final Values EMPTY = new Values() {
		@Override
		public void setValue(String name, Object value) {
			throw new UnsupportedOperationException();
		}
	};

}
