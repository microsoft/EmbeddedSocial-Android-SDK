/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.exception;

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

}
