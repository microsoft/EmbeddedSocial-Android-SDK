/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.sync.exception;

/**
 * Is thrown if synchronization with the server fails.
 */
public class SynchronizationException extends Exception {

	public SynchronizationException(String detailMessage) {
		super(detailMessage);
	}

	public SynchronizationException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public SynchronizationException(Throwable throwable) {
		super(throwable);
	}
}
