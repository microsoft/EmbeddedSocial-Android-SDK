/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.fetcher;

/**
 * Exception indicating that an empty string was return from the server instead of data.
 */
public class EmptyDataException extends Exception {

	public EmptyDataException(String message) {
		super(message);
	}
}
