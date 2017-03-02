/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.base.service;

/**
 * Is thrown when a service encounters unknown intent action.
 */
public class UnknownActionException extends Exception {

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates an instance.
	 * @param action	the action that is unknown to the service.
	 */
	public UnknownActionException(String action) {
		super("Unknown action: " + action);
	}
}
