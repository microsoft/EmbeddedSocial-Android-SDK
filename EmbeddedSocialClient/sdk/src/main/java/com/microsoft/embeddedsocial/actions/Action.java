/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.actions;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Some program's action (with progress visible in UI).
 */
public class Action {

	/**
	 * Action tag constants.
	 */
	public static class Tags {
		public static final String SIGN_IN = "signIn";
		public static final String SIGN_OUT = "signOut";
		public static final String CREATE_ACCOUNT = "createAccount";
		public static final String UPDATE_ACCOUNT = "updateAccount";
		public static final String DELETE_ACCOUNT = "deleteAccount";
		public static final String GET_COMMENT = "getComment";
		public static final String GET_REPLY = "getReply";
		public static final String REMOVE_FOLLOWER = "removeFollower";
	}

	private static AtomicLong NEXT_ID = new AtomicLong();

	private final long id = NEXT_ID.incrementAndGet();
	private final String tag;

	private boolean failed;
	private String error;

	/**
	 * Creates a new instance.
	 *
	 * @param tag string id of the action set this action belongs to; it can be used for action filtering
	 */
	Action(String tag) {
		this.tag = tag;
	}

	/**
	 * Marks this action as running.
	 */
	public void start() {
		OngoingActions.add(this);
	}

	/**
	 * Marks this action as completed successfully.
	 */
	public void complete() {
		OngoingActions.notifyCompleted(this);
	}

	/**
	 * Marks this action as completed with an error.
	 * @param errorMessage error message
	 */
	public void fail(String errorMessage) {
		failed = true;
		error = errorMessage;
		complete();
	}

	/**
	 * Marks this action as completed with an error (error message is empty).
	 */
	public void fail() {
		fail(null);
	}

	/**
	 * Returns whether this action is completed (successfully or not).
	 */
	public boolean isCompleted() {
		return OngoingActions.isCompleted(this);
	}

	/**
	 * Returns an error message (passed to {@link #fail(String)} method.
	 */
	public String getError() {
		return error;
	}

	/**
	 * Returns whether this action failed.
	 */
	public boolean isFailed() {
		return failed;
	}

	/**
	 * Returns the id of this action (unique among all actions). The action can be found by id via {@link OngoingActions#findActionById(Long)}
	 */
	public long getId() {
		return id;
	}

	/**
	 * Returns action's tag.
	 */
	public String getTag() {
		return tag;
	}

	// XXX: assume Action's are equal if their ids are equal
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Action action = (Action) o;
		return id == action.id;
	}

	@Override
	public int hashCode() {
		return (int) (id);
	}
}
