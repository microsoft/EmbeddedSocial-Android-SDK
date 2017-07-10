/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.base.utils.thread;

import android.support.annotation.NonNull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementation of {@link java.util.concurrent.ThreadFactory} that allows to specify priority of
 * threads.
 */
public class BackgroundThreadFactory implements ThreadFactory {

	private static final String DEFAULT_THREAD_NAME = "WORKER_THREAD_";
	private static final int DEFAULT_THREAD_PRIORITY = Thread.NORM_PRIORITY;

	private final AtomicInteger threadCounter = new AtomicInteger(0);
	private final int threadPriority;
	private final String threadNamePrefix;

	/**
	 * Creates an instance.
	 *
	 * @param threadPriority   priority for all threads created by this factory
	 * @param threadNamePrefix thread name prefix for all threads created by this factory.
	 *                         The unique number of each thread is added to thread name prefix automatically.
	 */
	public BackgroundThreadFactory(int threadPriority, String threadNamePrefix) {
		this.threadPriority = threadPriority;
		this.threadNamePrefix = threadNamePrefix;
	}

	/**
	 * Creates an instance.
	 *
	 * @param threadPriority   priority for all threads created by this factory
	 */
	public BackgroundThreadFactory(int threadPriority) {
		this(threadPriority, DEFAULT_THREAD_NAME);
	}

	/**
	 * Creates an instance.
	 *
	 * @param threadNamePrefix thread name prefix for all threads created by this factory
	 */
	public BackgroundThreadFactory(String threadNamePrefix) {
		this(DEFAULT_THREAD_PRIORITY, threadNamePrefix);
	}

	/**
	 * Creates an instance with default parameters.
	 */
	public BackgroundThreadFactory() {
		this(DEFAULT_THREAD_PRIORITY, DEFAULT_THREAD_NAME);
	}

	@Override
	public Thread newThread(@NonNull Runnable r) {
		Thread thread = new Thread(r);

		thread.setPriority(threadPriority);
		thread.setName(threadNamePrefix + threadCounter.incrementAndGet());
		thread.setDaemon(true);

		return thread;
	}
}
