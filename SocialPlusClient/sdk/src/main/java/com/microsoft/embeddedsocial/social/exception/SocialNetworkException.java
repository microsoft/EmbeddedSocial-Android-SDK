/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.social.exception;

public class SocialNetworkException extends Exception {

	public SocialNetworkException(String detailMessage) {
		super(detailMessage);
	}

	public SocialNetworkException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public SocialNetworkException(Throwable throwable) {
		super(throwable);
	}
}
