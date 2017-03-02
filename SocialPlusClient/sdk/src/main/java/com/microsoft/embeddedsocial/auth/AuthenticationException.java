/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.auth;

public class AuthenticationException extends Exception {

	public AuthenticationException(String detailMessage) {
		super(detailMessage);
	}

	public AuthenticationException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public AuthenticationException(Throwable throwable) {
		super(throwable);
	}
}
