/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.socialnetwork;

/**
* Encapsulates part of error response from Microsoft Live social network.
*/
public class MicrosoftLiveError {
	private String code;
	private String message;

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}
}
