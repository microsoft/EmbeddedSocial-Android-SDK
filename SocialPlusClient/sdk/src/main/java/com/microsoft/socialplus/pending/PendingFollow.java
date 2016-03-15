/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.pending;

import android.content.Context;

import com.microsoft.socialplus.data.storage.UserActionProxy;

/**
 * Pending "follow user" action.
 */
public class PendingFollow implements PendingAction {

	private final String userHandle;

	public PendingFollow(String userHandle) {
		this.userHandle = userHandle;
	}

	@Override
	public void execute(Context context) {
		new UserActionProxy(context).followUser(userHandle);
	}
}
