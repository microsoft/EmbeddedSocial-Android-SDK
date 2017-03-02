/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.util.menu;

import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;

import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.autorest.models.FollowerStatus;

/**
 * Helper for work with user context menu.
 */
public final class UserContextMenuHelper {
	private UserContextMenuHelper() {
	}

	public static void inflateUserRelationshipContextMenu(@NonNull PopupMenu menu, FollowerStatus userRelationshipStatus) {
		switch (userRelationshipStatus) {
			case NONE:
				menu.inflate(R.menu.es_user_follow);
				menu.inflate(R.menu.es_user_block);
				break;
			case PENDING:
			case FOLLOW:
				menu.inflate(R.menu.es_user_unfollow);
				menu.inflate(R.menu.es_user_block);
				break;
			case BLOCKED:
				menu.inflate(R.menu.es_user_unblock);
				break;
		}
	}
}
