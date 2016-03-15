/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus;

import android.app.Application;
import android.os.StrictMode;

import com.microsoft.socialplus.base.utils.debug.DebugLog;
import com.microsoft.socialplus.sdk.SocialPlus;

import java.lang.Thread.UncaughtExceptionHandler;

/**
 * Application class for the project.
 */
public class SocialPlusApplication extends Application implements UncaughtExceptionHandler {

	private static final UncaughtExceptionHandler DEFAULT_EXCEPTION_HANDLER = Thread.getDefaultUncaughtExceptionHandler();

	@Override
	public void onCreate() {
		super.onCreate();
		if (BuildConfig.DEBUG) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectAll()
				.penaltyLog()
				.build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects()
				.detectLeakedClosableObjects()
				.penaltyLog()
				.build());
		}
		Thread.setDefaultUncaughtExceptionHandler(this);
		SocialPlus.init(this, R.raw.social_plus_config);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		DebugLog.logExceptionWithStackTrace(ex);
		DEFAULT_EXCEPTION_HANDLER.uncaughtException(thread, ex);
	}


}
