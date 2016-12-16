/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.pending;

import android.content.Context;

import com.microsoft.embeddedsocial.data.storage.UserActionProxy;

/**
 * Pending "block user" action.
 */
public class PendingBlock implements PendingAction {

	private final String userHandle;

	public PendingBlock(String userHandle) {
		this.userHandle = userHandle;
	}

	@Override
	public void execute(Context context) {
		new UserActionProxy(context).blockUser(userHandle);
	}
}
