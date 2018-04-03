/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage.exception;

/**
 * A fatal exception related to the database after which the application can't recover.
 */
public class FatalDatabaseException extends RuntimeException {

	public FatalDatabaseException(String detailMessage) {
		super(detailMessage);
	}

	public FatalDatabaseException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public FatalDatabaseException(Throwable throwable) {
		super(throwable);
	}
}
