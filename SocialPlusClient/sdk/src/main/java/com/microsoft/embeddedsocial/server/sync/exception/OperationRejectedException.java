/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.sync.exception;

/**
 * Is thrown when the server rejects synchronization operation as invalid.
 */
public class OperationRejectedException extends SynchronizationException {

	public OperationRejectedException(String detailMessage) {
		super(detailMessage);
	}

	public OperationRejectedException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public OperationRejectedException(Throwable throwable) {
		super(throwable);
	}
}
