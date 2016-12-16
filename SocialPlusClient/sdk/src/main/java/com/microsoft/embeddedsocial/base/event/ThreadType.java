/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.base.event;


/**
 * Type of thread that processes an event.
 */
public enum ThreadType {
	
	/**
	 * Default handling thread - event bus defines thread type.
	 */
	DEFAULT,
	
	/**
	 * The event is processed in the calling thread.
	 */
	CALLING,

	/**
	 * The same as {@link #CALLING} but can be submitted only from the main thread.
	 */
	CALLING_MAIN,

	/**
	 * The same as {@link #CALLING} but can be submitted only from a background thread.
	 */
	CALLING_BACKGROUND,

	/**
	 * The event is processed in main thread.
	 */
	MAIN,
	
	/**
	 * The event is processed in the background thread pool of event bus.
	 */
	BACKGROUND
}
