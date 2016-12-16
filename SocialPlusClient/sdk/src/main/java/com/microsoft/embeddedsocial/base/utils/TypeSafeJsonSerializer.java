/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.base.utils;

import com.google.gson.Gson;

/**
 * Util class converting object to/from strings, keeps exact class type during convering.
 */
public class TypeSafeJsonSerializer {

	private static final char DELIMITER = '/';
	private final Gson gson;

	public TypeSafeJsonSerializer() {
		this(new Gson());
	}

	public TypeSafeJsonSerializer(Gson gson) {
		this.gson = gson;
	}

	public String valueToString(Object value) {
		String objectClassName = value.getClass().getName();
		String objectJsonValue = gson.toJson(value);
		return objectClassName + DELIMITER + objectJsonValue;
	}

	@SuppressWarnings("unchecked")
	public <T> T parseValue(String s) throws ClassNotFoundException {
		int pos = s.indexOf(DELIMITER);
		if (pos < 0) {
			throw new IllegalArgumentException("Wrong data format");
		}
		String objectClassName = s.substring(0, pos);
		Class<?> objectClass = Class.forName(objectClassName);
		String objectJsonValue = s.substring(pos + 1);
		return (T) gson.fromJson(objectJsonValue, objectClass);
	}

}
