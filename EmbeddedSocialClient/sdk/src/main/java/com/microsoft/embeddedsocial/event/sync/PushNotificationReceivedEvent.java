/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.event.sync;

import com.microsoft.embeddedsocial.base.event.AbstractEvent;

/**
 * Is raised when a push notification from the server is received.
 */
public class PushNotificationReceivedEvent extends AbstractEvent {

	private final String text;

	public PushNotificationReceivedEvent(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}
}
