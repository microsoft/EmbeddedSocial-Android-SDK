/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.exception;

/**
 * Indicates error during performing a request to the server (either server or network error).
 */
public class NetworkRequestException extends Exception {

	public NetworkRequestException() {
	}

	public NetworkRequestException(String detailMessage) {
		super(detailMessage);
	}

	public NetworkRequestException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public NetworkRequestException(Throwable throwable) {
		super(throwable);
	}

	public static NetworkRequestException generateException(int statusCode, String message) {
		switch (statusCode) {
			case BadRequestException.STATUS_CODE:
				return new BadRequestException(message);
			case ForbiddenException.STATUS_CODE:
				return new ForbiddenException(message);
			case NotFoundException.STATUS_CODE:
				return new NotFoundException(message);
			case ConflictException.STATUS_CODE:
				return new ConflictException(message);
			case InternalServerException.STATUS_CODE:
				return new InternalServerException(message);
			case ServiceUnavailableException.STATUS_CODE:
			 	return new ServiceUnavailableException(message);
			default: // no detail provided
				return new NetworkRequestException(message);
		}
	}
}
