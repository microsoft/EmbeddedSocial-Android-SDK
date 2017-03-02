/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.exception;

/**
 *
 */
public class ResourceAlreadyExistsException extends StatusException {

	public static final int STATUS_CODE = 409;

	public ResourceAlreadyExistsException(String message, Throwable cause) {
		super(STATUS_CODE, message, cause);
	}

	public ResourceAlreadyExistsException(Throwable cause) {
		super(STATUS_CODE, cause);
	}
}
