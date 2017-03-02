/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.social.exception;

public class InvalidCredentialsException extends SocialNetworkException {

	public InvalidCredentialsException(String detailMessage) {
		super(detailMessage);
	}

	public InvalidCredentialsException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public InvalidCredentialsException(Throwable throwable) {
		super(throwable);
	}
}
