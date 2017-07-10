/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.exception;

/**
 *
 */
public class UnauthorizedException extends StatusException {

	public static final int STATUS_CODE = 401;

	public UnauthorizedException(String message) {
		super(STATUS_CODE, message);
	}

	public UnauthorizedException(String message, Throwable cause) {
		super(STATUS_CODE, message, cause);
	}

	public UnauthorizedException(Throwable cause) {
		super(STATUS_CODE, cause);
	}
}
