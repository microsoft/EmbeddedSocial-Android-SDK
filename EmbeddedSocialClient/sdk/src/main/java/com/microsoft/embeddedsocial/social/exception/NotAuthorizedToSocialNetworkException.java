/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.social.exception;

public class NotAuthorizedToSocialNetworkException extends SocialNetworkException {

	public NotAuthorizedToSocialNetworkException(String detailMessage) {
		super(detailMessage);
	}

	public NotAuthorizedToSocialNetworkException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public NotAuthorizedToSocialNetworkException(Throwable throwable) {
		super(throwable);
	}

	public NotAuthorizedToSocialNetworkException() {
		super("Not authorized");
	}
}
