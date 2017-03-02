/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.base.event;

import android.os.Handler;
import android.os.Looper;

import com.microsoft.embeddedsocial.base.utils.thread.BackgroundThreadFactory;
import com.microsoft.embeddedsocial.base.utils.thread.ThreadUtils;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The event bus allowing to listen to or to produce messages (events).
 */
public final class EventBus {

	private static final int WORKER_THREADS = 2;

	private static Bus bus = new Bus(ThreadEnforcer.ANY);
	private static Handler mainThreadHandler = new Handler(Looper.getMainLooper());
	private static ExecutorService executor = Executors.newFixedThreadPool(
			WORKER_THREADS,
			new BackgroundThreadFactory("EVENT_BUS")
	);
	
	private EventBus() {  }
	
	/**
	 * Posts a new event to the bus. The thread which will be used to
	 * process the event will be defined by the event.
	 * @param	event	the event to post
	 */
	public static void post(AbstractEvent event) {
		ThreadType eventThreadType = getEventThreadType(event.getClass());
		checkSubmitterThread(event, eventThreadType);
		post(event, eventThreadType);
	}

	private static void checkSubmitterThread(AbstractEvent event, ThreadType threadType) {
		if (threadType == ThreadType.CALLING_BACKGROUND && ThreadUtils.inMainThread()) {
			throw new RuntimeException(event.getClass().getName() + " should be posted only in a background thread");
		} else if (threadType == ThreadType.CALLING_MAIN && !ThreadUtils.inMainThread()) {
			throw new RuntimeException(event.getClass().getName() + " should be posted only in the main thread");
		}
	}

	/**
	 * Posts a new event to the bus.
	 * @param	event				the event to post
	 * @param	handlingThreadType	type of thread that must handle the event
	 */
	private static void post(Object event, ThreadType handlingThreadType) {
		switch (handlingThreadType) {
				
			case MAIN:
				mainThreadHandler.post(new PostEventTask(event));
				break;
				
			case BACKGROUND:
				executor.execute(new PostEventTask(event));
				break;

			case CALLING:
			case CALLING_BACKGROUND:
			case CALLING_MAIN:
			case DEFAULT:
			default:
				bus.post(event);
				break;
		}
	}
	
	/**
	 * Registers a new bus listener.
	 * @param listener	the listener to register
	 */
	public static void register(Object listener) {
		bus.register(listener);
	}
	
	/**
	 * Unregisters a listener previously registered via {@link #register(Object)}.
	 * @param listener	the listener to unregister
	 */
	public static void unregister(Object listener) {
		bus.unregister(listener);
	}
	
	private static ThreadType getEventThreadType(Class<?> eventClass) {
		HandlingThread annotation = eventClass.getAnnotation(HandlingThread.class);
		ThreadType result;
		
		if (annotation != null) {
			result = annotation.value();
		} else {
			result = ThreadType.DEFAULT;
		}
		
		return result;
	}
	
	/**
	 * Posts events to the bus.
	 */
	private static final class PostEventTask implements Runnable {
		
		private final Object message;
		
		PostEventTask(Object message) {
			this.message = message;
		}

		@Override
		public void run() {
			bus.post(message);
		}
	}	
}
