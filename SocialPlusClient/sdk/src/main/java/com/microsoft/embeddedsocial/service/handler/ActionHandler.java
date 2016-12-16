/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.service.handler;

import android.content.Intent;

import com.microsoft.embeddedsocial.actions.OngoingActions;
import com.microsoft.embeddedsocial.base.service.IServiceIntentHandler;
import com.microsoft.embeddedsocial.actions.Action;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.service.IntentExtras;
import com.microsoft.embeddedsocial.service.ServiceAction;

/**
 * Base class for {@link IServiceIntentHandler} implementation dealing with {@link Action}.
 */
public abstract class ActionHandler implements IServiceIntentHandler<ServiceAction> {

	@Override
	public final void handleIntent(ServiceAction serviceAction, Intent intent) {
		long actionId = intent.getLongExtra(IntentExtras.ACTION_ID, -1);
		Action action = OngoingActions.findActionById(actionId);
		if (action == null) {
			DebugLog.e("Action is null");
			return;
		}
		try {
			handleAction(action, serviceAction, intent);
		} finally {
			if (!action.isCompleted()) {
				action.complete();
			}
		}
	}

	protected abstract void handleAction(Action action, ServiceAction serviceAction, Intent intent);

	@Override
	public void dispose() {

	}
}
