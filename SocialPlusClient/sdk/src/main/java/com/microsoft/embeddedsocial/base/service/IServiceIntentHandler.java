/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.base.service;

import android.content.Intent;

import com.microsoft.embeddedsocial.base.IDisposable;

/**
 * Handles a specific type of intents.
 * @param <T>	action type
 */
public interface IServiceIntentHandler<T extends Enum<T>> extends IDisposable {

	/**
	 * Processes the intent.
	 * @param	action	the action
	 * @param	intent	the intent to process
	 */
	void handleIntent(T action, Intent intent);
}
