/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.base.service;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.base.utils.thread.BackgroundThreadFactory;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Processes incoming intents.
 *
 * @param <T> enum type used to parse intent actions
 */
public final class IntentProcessor<T extends Enum<T>> implements IServiceIntentProcessor {

	private static final int DEFAULT_WORKER_THREADS = 4;

	private final ExecutorService executor;
	private final Map<T, IServiceIntentHandler<T>> intentHandlers;
	private final Class<T> enumClass;
	private final String packageName;
	private final AtomicInteger taskCount = new AtomicInteger(0);
	private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());
	private IQueueStateListener queueStateListener;

	private boolean disposed;

	/**
	 * Creates an instance.
	 *
	 * @param    context        valid context
	 * @param    enumClass    class of the enum used to register intent handlers
	 */
	public IntentProcessor(Context context, Class<T> enumClass) {
		this(context, enumClass, DEFAULT_WORKER_THREADS, newDefaultThreadFactory());
	}

	/**
	 * Creates an instance.
	 *
	 * @param context   valid context
	 * @param enumClass class of the enum used to register intent handlers
	 * @param threads   number of worker threads
	 */
	public IntentProcessor(Context context, Class<T> enumClass, int threads) {
		this(context, enumClass, threads, newDefaultThreadFactory());
	}

	/**
	 * Creates an instance.
	 *
	 * @param context       valid context
	 * @param enumClass     class of the enum used to register intent handlers
	 * @param threads       number of worker threads
	 * @param threadFactory worker thread factory
	 */
	public IntentProcessor(Context context, Class<T> enumClass, int threads,
						   ThreadFactory threadFactory) {

		this.executor = Executors.newFixedThreadPool(threads, threadFactory);
		this.packageName = context.getPackageName();
		this.enumClass = enumClass;
		this.intentHandlers = new EnumMap<>(enumClass);
	}

	/**
	 * Convenience method to create a single-threaded intent processor.
	 *
	 * @param context   valid context
	 * @param enumClass class of the enum used to register intent handlers
	 * @return    {@link IntentProcessor} instance.
	 */
	public static <T extends Enum<T>> IntentProcessor<T> newSingleThreadIntentProcessor(
			Context context, Class<T> enumClass) {

		return new IntentProcessor<>(context, enumClass, 1);
	}

	private static BackgroundThreadFactory newDefaultThreadFactory() {
		return new BackgroundThreadFactory("intent processor #");
	}

	/**
	 * Registers intent handler for a specific type of intents.
	 *
	 * @param    action            service action
	 * @param    handler            {@link IServiceIntentHandler} instance
	 */
	public void registerIntentHandler(T action, IServiceIntentHandler<T> handler) {
		intentHandlers.put(action, handler);
	}

	/**
	 * Asynchronously processes incoming intents using registered intent handlers.
	 * <b>Should always be called in UI thread.</b>
	 *
	 * @param    intent    the intent to process
	 * @return    <code>true</code> if the handler for this intent action is registered
	 * and the intent can be processed, <code>false</code> otherwise.
	 * @see        {@link #registerIntentHandler(Enum, IServiceIntentHandler)}
	 */
	@Override
	public boolean processIntentAsync(Intent intent) {

		if (!isRunningInMainThread()) {
			throw new IllegalStateException("this method should be called from main thread");
		}

		T action;

		try {
			action = getIntentAction(intent);
		} catch (UnknownActionException e) {
			DebugLog.logException(e);
			return false;
		}

		IServiceIntentHandler<T> handler = intentHandlers.get(action);
		boolean result;

		if (handler != null) {
			DebugLog.i("incoming intent: " + action);
			onTaskStarted();
			try {
				executor.execute(new IntentHandlerAdapter(handler, action, intent));
			} catch (RejectedExecutionException e) {
				onTaskEnded();
			}
			result = true;
		} else {
			result = false;
		}

		return result;
	}

	@Override
	public synchronized void dispose() {
		if (!disposed) {
			disposed = true;
			executor.shutdownNow();
			disposeIntentHandlers();
		}
	}

	private void disposeIntentHandlers() {
		for (Entry<T, IServiceIntentHandler<T>> entry : intentHandlers.entrySet()) {
			IServiceIntentHandler<?> handler = entry.getValue();
			handler.dispose();
		}
	}

	private T getIntentAction(Intent intent) throws UnknownActionException {
		String action = intent.getAction();
		String packagePrefix = packageName + '.';

		if (!TextUtils.isEmpty(action) && action.startsWith(packagePrefix)) {
			String cleanAction = action.substring(packagePrefix.length()).toUpperCase();
			try {
				return Enum.valueOf(enumClass, cleanAction);
			} catch (IllegalArgumentException e) {
				throw new UnknownActionException(action);
			}
		} else {
			throw new UnknownActionException(action);
		}
	}

	@Override
	public void setQueueStateListener(IQueueStateListener listener) {
		this.queueStateListener = listener;
	}

	private void onTaskStarted() {
		taskCount.incrementAndGet();
	}

	private synchronized void onTaskEnded() {
		int tasks = taskCount.decrementAndGet();
		if (tasks == 0) {
			onTaskQueueIsEmpty();
		}
	}

	private boolean isRunningInMainThread() {
		return Looper.myLooper() == Looper.getMainLooper();
	}

	private void onTaskQueueIsEmpty() {
		mainThreadHandler.post(() -> {
			if (queueStateListener != null && taskCount.get() == 0) {
				queueStateListener.onTaskQueueIsEmpty();
			}
		});
	}

	/**
	 * Provides {@link Runnable} interface implementation
	 * for {@link IServiceIntentHandler} instances.
	 */
	private final class IntentHandlerAdapter implements Runnable {

		private final IServiceIntentHandler<T> handler;
		private final T action;
		private final Intent intent;

		IntentHandlerAdapter(IServiceIntentHandler<T> handler, T action, Intent intent) {
			this.handler = handler;
			this.action = action;
			this.intent = intent;
		}

		@Override
		public void run() {
			try {
				handler.handleIntent(action, intent);
			} catch (Exception e) {
				DebugLog.logExceptionWithStackTrace(e);
			} finally {
				onTaskEnded();
			}
		}
	}
}
