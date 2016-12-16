/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.actions;

/**
 * Determines which actions are interesting for an application's component.
 */
public interface ActionFilter {

	/**
	 * Whether the action is interesting for a component.
	 */
	boolean filter(Action action);

}
