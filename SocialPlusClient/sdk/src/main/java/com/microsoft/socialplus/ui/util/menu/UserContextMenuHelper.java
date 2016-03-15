/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.ui.util.menu;

import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;

import com.microsoft.autorest.models.FollowerStatus;
import com.microsoft.socialplus.sdk.R;

/**
 * Helper for work with user context menu.
 */
public final class UserContextMenuHelper {
	private UserContextMenuHelper() {
	}

	public static void inflateUserRelationshipContextMenu(@NonNull PopupMenu menu, FollowerStatus userRelationshipStatus) {
		switch (userRelationshipStatus) {
			case NONE:
				menu.inflate(R.menu.sp_user_follow);
				menu.inflate(R.menu.sp_user_block);
				break;
			case PENDING:
			case FOLLOW:
				menu.inflate(R.menu.sp_user_unfollow);
				menu.inflate(R.menu.sp_user_block);
				break;
			case BLOCKED:
				menu.inflate(R.menu.sp_user_unblock);
				break;
		}
	}
}
