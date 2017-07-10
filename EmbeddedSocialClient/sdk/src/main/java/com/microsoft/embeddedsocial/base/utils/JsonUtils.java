/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.base.utils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;

/**
 * Helper singleton class to work with Gson library.
 */
public final class JsonUtils {
	private final Gson gson;
	private static JsonUtils instance;
	
	private JsonUtils() {
		gson = new Gson();
	}
	
	private static synchronized JsonUtils getInstance() {
		if (instance == null) {
			instance = new JsonUtils();
		}
		
		return instance;
	}
	
	public static synchronized <T> T fromJson(String json, Class<T> classOfT) {
		T result = null;
		try {
			result = getInstance().gson.fromJson(json, classOfT);
		} catch (JsonSyntaxException e) {
			DebugLog.e(e.getMessage());
		}
		
		return result;
	}
	
	public static synchronized String toJson(Object src) {
		return getInstance().gson.toJson(src);
	}
}
