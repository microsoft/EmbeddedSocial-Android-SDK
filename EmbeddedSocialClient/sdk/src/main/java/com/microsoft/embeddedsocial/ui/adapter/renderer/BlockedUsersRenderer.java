/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.adapter.renderer;

import android.content.Context;
import android.view.ViewGroup;

import com.microsoft.embeddedsocial.account.UserAccount;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.server.model.view.UserCompactView;
import com.microsoft.embeddedsocial.ui.adapter.viewholder.UserListItemHolder;

/**
 * Renders blocked users.
 */
public class BlockedUsersRenderer extends BaseUserRenderer {

	public BlockedUsersRenderer(Context context) {
		super(context);
	}

	@Override
	protected void onItemRendered(UserCompactView user, UserListItemHolder holder) {
		super.onItemRendered(user, holder);
		if (user.isUnblocked()) {
			renderUnblockedUser(user, holder);
		} else {
			renderBlockedUser(user, holder);
		}
	}

	private void renderBlockedUser(UserCompactView user, UserListItemHolder holder) {
		holder.actionButton.setText(R.string.es_button_unblock);
		holder.actionButton.setOnClickListener(v -> {
			UserAccount.getInstance().unblockUser(user.getHandle());
			user.setUnblocked(true);
			renderUnblockedUser(user, holder);
		});
	}

	private void renderUnblockedUser(UserCompactView user, UserListItemHolder holder) {
		holder.actionButton.setText(R.string.es_button_block);
		holder.actionButton.setOnClickListener(v -> {
			UserAccount.getInstance().blockUser(user.getHandle());
			user.setUnblocked(false);
			renderBlockedUser(user, holder);
		});
	}

	@Override
	public UserListItemHolder createViewHolder(ViewGroup parent) {
		UserListItemHolder holder = super.createViewHolder(parent);
		getStyleHelper().applyGrayStyle(holder.actionButton);
		return holder;
	}
}
