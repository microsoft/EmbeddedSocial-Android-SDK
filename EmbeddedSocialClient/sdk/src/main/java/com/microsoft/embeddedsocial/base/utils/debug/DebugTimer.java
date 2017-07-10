/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.base.utils.debug;

import java.util.ArrayList;
import java.util.List;

/**
 * Is used to measure time spent on any actions.
 */
public final class DebugTimer {

	private DebugTimer() {
	}

	private static final ThreadLocal<List<TimeInterval>> EVENTS = new ThreadLocal<List<TimeInterval>>() {
		@Override
		protected List<TimeInterval> initialValue() {
			return new ArrayList<>();
		}
	};

	/**
	 * Marks the start of time measuring interval for an event.
	 *
	 * @param event caption of the event
	 */
	public static void startInterval(String event) {
		DebugLog.w("started " + event);
		List<TimeInterval> threadEvents = EVENTS.get();
		threadEvents.add(new TimeInterval(event));
	}

	/**
	 * Ends last started time measuring interval. Can be called only after a call to
	 * {@link #startInterval(String)}.
	 */
	public static void endInterval() {
		long now = System.currentTimeMillis();
		List<TimeInterval> threadEvents = EVENTS.get();
		if (threadEvents.isEmpty()) {
			throw new IllegalStateException("endInterval() called without startInterval()");
		}
		TimeInterval interval = threadEvents.remove(threadEvents.size() - 1);
		DebugLog.w("ended " + interval.event + ", " + (now - interval.timestamp) + " ms");
	}

	/**
	 * Represents a time interval for some event.
	 */
	private static final class TimeInterval {

		final long timestamp = System.currentTimeMillis();
		final String event;

		TimeInterval(String caption) {
			this.event = caption;
		}
	}
}
