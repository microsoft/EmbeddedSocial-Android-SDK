/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.base.event;


/**
 * Base class for all event bus events.
 */
public abstract class AbstractEvent {

	/**
	 * Empty constructor.
	 */
	protected AbstractEvent() {
	}

	/**
	 * Submits the event.
	 */
	public final void submit() {
		EventBus.post(this);
	}
}
