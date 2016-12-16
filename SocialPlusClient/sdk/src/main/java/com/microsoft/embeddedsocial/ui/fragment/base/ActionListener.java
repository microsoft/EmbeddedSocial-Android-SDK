/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.fragment.base;

import com.microsoft.embeddedsocial.actions.Action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Listens for actions state.
 */
@SuppressWarnings("UnusedParameters")
public abstract class ActionListener {

	private List<Action> ongoingActions = new LinkedList<>();

	protected void onActionStarted(Action action) {
	}

	protected void onActionCompleted(Action action) {
	}

	protected void onActionSucceeded(Action action) {
	}

	protected void onActionFailed(Action action, String error) {
	}

	protected void onActionsCompletionMissed(List<Action> completedActions, List<Action> succeededActions, List<Action> failedActions) {
	}

	void notifyActionStarted(Action action) {
		ongoingActions.add(action);
		onActionStarted(action);
	}

	void notifyActionCompleted(Action action) {
		ongoingActions.remove(action);
		if (action.isFailed()) {
			onActionFailed(action, action.getError());
		} else {
			onActionSucceeded(action);
		}
		onActionCompleted(action);
	}

	void notifyOnResume() {
		if (!ongoingActions.isEmpty()) {
			List<Action> completedActions = new ArrayList<>();
			List<Action> succeededActions = new ArrayList<>();
			List<Action> failedActions = new ArrayList<>();
			Iterator<Action> iterator = ongoingActions.iterator();
			while (iterator.hasNext()) {
				Action action = iterator.next();
				if (action.isCompleted()) {
					completedActions.add(action);
					if (action.isFailed()) {
						failedActions.add(action);
					} else {
						succeededActions.add(action);
					}
					iterator.remove();
				}
			}
			if (!completedActions.isEmpty()) {
				onActionsCompletionMissed(completedActions, succeededActions, failedActions);
			}
		}
	}
}
