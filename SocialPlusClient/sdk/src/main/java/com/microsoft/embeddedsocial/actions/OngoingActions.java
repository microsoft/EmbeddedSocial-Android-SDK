/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.actions;

import com.microsoft.embeddedsocial.base.event.EventBus;
import com.microsoft.embeddedsocial.event.action.ActionCompletedEvent;
import com.microsoft.embeddedsocial.event.action.ActionStartedEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Holds information about currently running actions and their errors.
 */
public final class OngoingActions {

	private static final Map<Long, Action> ACTIONS = new HashMap<>();
	private static final Map<Action, Boolean> COMPLETED_ACTIONS = new WeakHashMap<>();

	private OngoingActions() {
	}

	/**
	 * Finds a currently running (i.e. started and not completed) action by it's id
	 *
	 * @param id action's id
	 */
	public static Action findActionById(Long id) {
		synchronized (OngoingActions.class) {
			return ACTIONS.get(id);
		}
	}

	/**
	 * Returns whether there is a running action satisfying the filter.
	 * @param filter action filter
	 */
	public static boolean hasActions(ActionFilter filter) {
		synchronized (OngoingActions.class) {
			for (Action action : ACTIONS.values()) {
				if (filter.filter(action)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns whether there is a running action with such tag.
	 */
	public static boolean hasActionsWithTag(String tag) {
		return hasActions(new ActionTagFilter(tag));
	}

	/**
	 * Register the action as running.
	 */
	static void add(Action action) {
		synchronized (OngoingActions.class) {
			if (ACTIONS.containsKey(action.getId())) {
				throw new RuntimeException("Action has been already started");
			}
			ACTIONS.put(action.getId(), action);
		}
		EventBus.post(new ActionStartedEvent(action));
	}

	/**
	 * returns whether the action is already completed.
	 */
	static boolean isCompleted(Action action) {
		synchronized (OngoingActions.class) {
			return COMPLETED_ACTIONS.containsKey(action);
		}
	}

	/**
	 * Marks the action as completed.
	 */
	static void notifyCompleted(Action action) {
		synchronized (OngoingActions.class) {
			if (!ACTIONS.containsKey(action.getId())) {
				throw new RuntimeException("Action has been already completed");
			}
			ACTIONS.remove(action.getId());
			COMPLETED_ACTIONS.put(action, Boolean.TRUE);
		}
		EventBus.post(new ActionCompletedEvent(action));
	}

}
