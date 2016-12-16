/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.event;

import android.content.pm.PackageManager;

import com.microsoft.embeddedsocial.base.event.AbstractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Is raised when a permission request result is delivered to an activity.
 */
public class PermissionRequestResultEvent extends AbstractEvent {

	private final Map<String, Boolean> requestResults = new HashMap<>();
	private final int requestCode;

	/**
	 * Creates an instance.
	 * @param requestCode   permission request code
	 * @param permissions   requested permissions
	 * @param grantResults  request results
	 */
	public PermissionRequestResultEvent(int requestCode, String[] permissions, int[] grantResults) {
		this.requestCode = requestCode;
		for (int i = 0; i < permissions.length; i++) {
			requestResults.put(permissions[i], grantResults[i] == PackageManager.PERMISSION_GRANTED);
		}
	}

	/**
	 * Gets the permissions that were requested.
	 * @return  set of permissions.
	 */
	public Set<String> getRequestedPermissions() {
		return requestResults.keySet();
	}

	/**
	 * Checks if a permission was requested and granted.
	 * @param permission    the permission to check
	 * @return  true if permission was requested and granted.
	 */
	public boolean isPermissionGranted(String permission) {
		return requestResults.containsKey(permission) ? requestResults.get(permission) : false;
	}

	/**
	 * Gets permission request code.
	 * @return  permission request code.
	 */
	public int getRequestCode() {
		return requestCode;
	}
}
