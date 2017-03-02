/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.data.Preferences;

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
	 * returns instance id value (saved or newly generated)
	 * @return
	 */
	public String getInstanceId() {
		return InstanceHandleHolder.INSTANCE_ID;
	}

	/**
	 * returns current Telephony manager network type
	 * @return
	 */
	public int getNetworkType() {
		return telephonyManager.getNetworkType();
	}

	private static class InstanceHandleHolder {

		private static final String INSTANCE_ID = getInstanceId();

		private static String getInstanceId() {
			Preferences preferences = GlobalObjectRegistry.getObject(Preferences.class);
			String instanceId = preferences.getInstanceId();
			if (TextUtils.isEmpty(instanceId)) {
				instanceId = UUID.randomUUID().toString();
				preferences.setInstanceId(instanceId);
			}
			return instanceId;
		}
	}

}
