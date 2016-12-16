/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.account;

/**
 * Type of action caused an sign-in request.
 */
public enum AuthorizationCause {

	/**
	 * Caused by "Follow" option
	 */
	FOLLOW,

	/**
	 * Caused by "Block user" option.
	 */
	BLOCK,

	/**
	 * Caused by "Like" button
	 */
	LIKE,

	/**
	 * Caused by "Pin" button
	 */
	PIN,

	/**
	 * Caused by "Add comment" button
	 */
	COMMENT
}
