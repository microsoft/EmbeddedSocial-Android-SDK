/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.microsoft.socialplus.base.GlobalObjectRegistry;
import com.microsoft.socialplus.data.Preferences;

import java.util.UUID;

/**
 * Provides requests with some basic data
 */
public class RequestInfoProvider {

	private final TelephonyManager telephonyManager;

	/**
	 * Create instance
	 * @param context
	 */
	public RequestInfoProvider(Context context) {
		telephonyManager = (TelephonyManager)context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
	}

	/**
	 * returns instance handle value (saved or newly generated)
	 * @return
	 */
	public String getInstanceHandle() {
		return InstanceHandleHolder.INSTANCE_HANDLE;
	}

	/**
	 * returns current Telephony manager network type
	 * @return
	 */
	public int getNetworkType() {
		return telephonyManager.getNetworkType();
	}

	private static class InstanceHandleHolder {

		private static final String INSTANCE_HANDLE = getInstanceHandle();

		private static String getInstanceHandle() {
			Preferences preferences = GlobalObjectRegistry.getObject(Preferences.class);
			String instanceHandle = preferences.getInstanceHandle();
			if (TextUtils.isEmpty(instanceHandle)) {
				instanceHandle = UUID.randomUUID().toString();
				preferences.setInstanceHandle(instanceHandle);
			}
			return instanceHandle;
		}
	}

}
