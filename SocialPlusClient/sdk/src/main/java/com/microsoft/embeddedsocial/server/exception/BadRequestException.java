/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.exception;

/**
 *
 */
public class BadRequestException extends StatusException {

	public static final int STATUS_CODE = 400;

	public BadRequestException(String message) {
		super(STATUS_CODE, message);
	}

	public BadRequestException(String message, Throwable cause) {
		super(STATUS_CODE, message, cause);
	}

	public BadRequestException(Throwable cause) {
		super(STATUS_CODE, cause);
	}
}
