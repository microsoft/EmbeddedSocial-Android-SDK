/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.event.action;


import com.microsoft.embeddedsocial.actions.Action;
import com.microsoft.embeddedsocial.base.event.AbstractEvent;

/**
 * Base event class for actions.
 */
abstract class ActionEvent extends AbstractEvent {

	private final Action action;

	ActionEvent(Action action) {
		this.action = action;
	}

	public Action getAction() {
		return action;
	}
}
