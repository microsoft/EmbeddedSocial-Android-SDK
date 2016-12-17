/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.util.menu;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;

import com.microsoft.embeddedsocial.account.UserAccount;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.server.model.view.UserCompactView;

/**
 * Context menu for any content
 */
public class ContextMenuClickListener implements PopupMenu.OnMenuItemClickListener {
	protected Context context;
	protected UserCompactView user;
	protected String contentHandle;

	public ContextMenuClickListener(Context context, UserCompactView user, String contentHandle) {
		this.context = context;
		this.user = user;
		this.contentHandle = contentHandle;
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		int i = item.getItemId();
		if (i == R.id.es_actionFollow) {
			UserAccount.getInstance().followUser(user);
			return true;
		} else if (i == R.id.es_actionUnfollow) {
			UserAccount.getInstance().unfollowUser(user.getHandle());
			return true;
		} else if (i == R.id.es_actionBlockUser) {
			UserAccount.getInstance().blockUser(user.getHandle());
			return true;
		} else if (i == R.id.es_actionUnblockUser) {
			UserAccount.getInstance().unblockUser(user.getHandle());
			return true;
		} else {
			return false;
		}
	}
}
