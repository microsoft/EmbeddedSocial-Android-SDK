/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.pending;

import android.content.Context;

import com.microsoft.embeddedsocial.data.storage.UserActionProxy;

/**
 * Pending "pin" or "unpin" action.
 */
public class PendingPin implements PendingAction {

	private final String topicHandle;
	private final boolean pinned;

	public PendingPin(String topicHandle, boolean pinned) {
		this.topicHandle = topicHandle;
		this.pinned = pinned;
	}

	@Override
	public void execute(Context context) {
		new UserActionProxy(context).setPinStatus(topicHandle, pinned);
	}
}
