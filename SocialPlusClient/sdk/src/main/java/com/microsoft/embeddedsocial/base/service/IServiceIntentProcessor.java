/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.base.service;

import android.content.Intent;

import com.microsoft.embeddedsocial.base.IDisposable;


/**
 * Processes incoming intents.
 */
public interface IServiceIntentProcessor extends IDisposable {

	/**
	 * Listens to internal task queue state of the intent processor.
	 */
	interface IQueueStateListener {
	
		/**
		 * Is called when all tasks are processed. Runs on UI (main) thread.
		 */
		void onTaskQueueIsEmpty();
	}
	
	/**
	 * Asynchronously processes incoming intents using registered intent handlers.
	 * @param	intent	the intent to process
	 * @return	<code>true</code> if the handler for this intent action is registered
	 * and the intent can be processed, <code>false</code> otherwise.
	 */
	boolean processIntentAsync(Intent intent);

	/**
	 * Sets the listener receiving callbacks when processor queue state changes.
	 * @param listener	the listener to set
	 */	
	void setQueueStateListener(IQueueStateListener listener);
}
