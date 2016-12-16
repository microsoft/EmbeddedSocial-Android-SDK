/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.exception;

/**
 * Base class for network exceptions with status codes
 */
public class StatusException extends NetworkRequestException {

	private final int statusCode;

	public StatusException(int statusCode, String message) {
		super(message);
		this.statusCode = statusCode;
	}

	public StatusException(int statusCode, String message, Throwable cause) {
		super(message, cause);
		this.statusCode = statusCode;
	}

	public StatusException(int statusCode, Throwable cause) {
		super(cause);
		this.statusCode = statusCode;
	}

	/**
	 * HTTP status code returned by server
	 */
	public int getStatusCode() {
		return statusCode;
	}
}
