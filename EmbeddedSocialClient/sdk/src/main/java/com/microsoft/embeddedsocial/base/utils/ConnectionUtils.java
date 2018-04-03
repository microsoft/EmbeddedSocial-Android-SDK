/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.base.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Simple utils to check internet connection.
 */
public final class ConnectionUtils {
	private ConnectionUtils() {

	}

	public static boolean isConnectingToInternet(Context context) {
		final ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		}

		final NetworkInfo[] info = connectivity.getAllNetworkInfo();
		if (info == null) {
			return false;
		}

		for (NetworkInfo anInfo : info) {
			if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
				return true;
			}
		}

		return false;
	}
}
