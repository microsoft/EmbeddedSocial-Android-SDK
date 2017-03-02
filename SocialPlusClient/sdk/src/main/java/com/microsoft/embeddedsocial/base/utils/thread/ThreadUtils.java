/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.base.utils.thread;

import android.os.ConditionVariable;
import android.os.Handler;
import android.os.Looper;

/**
 * Convenient methods for work with threads.
 */
public final class ThreadUtils {

	private static final Handler MAIN_THREAD_HANDLER = new Handler(Looper.getMainLooper());

	private ThreadUtils() {
	}

	/**
	 * Checks that this method called from main thread.
	 */
	public static boolean inMainThread() {
		return Looper.getMainLooper().getThread() == Thread.currentThread();
	}

	/**
	 * Execute <code>task</code> in main thread.
	 *
	 * @param task {@link Runnable} to execute
	 */
	public static void runOnMainThread(Runnable task) {
		if (inMainThread()) {
			task.run();
		} else {
			MAIN_THREAD_HANDLER.post(task);
		}
	}

	/**
	 * Execute <code>task</code> in main thread synchronously (i.e. return only
	 * after task is executed.
	 *
	 * @param task {@link Runnable} to execute
	 * @throws InterruptedException if current thread was interrupted during execution
	 */
	public static void runOnMainThreadSync(final Runnable task) throws InterruptedException {
		if (inMainThread()) {
			task.run();
		} else {
			final ConditionVariable barrier = new ConditionVariable();
			MAIN_THREAD_HANDLER.post(() -> {
				task.run();
				barrier.open();
			});
			barrier.block();
		}
	}

	public static Handler getMainThreadHandler() {
		return MAIN_THREAD_HANDLER;
	}

	/**
	 * Throws an exception if is invoked NOT from the UI thread.
	 * @param errorMessage  error message to include in the exception
	 */
	public static void enforceMainThread(String errorMessage) {
		if (!inMainThread()) {
			throw new IllegalStateException(errorMessage);
		}
	}

	/**
	 * Throws an exception if is invoked NOT from the UI thread.
	 */
	public static void enforceMainThread() {
		enforceMainThread("this method can't be launched from background threads");
	}
}
