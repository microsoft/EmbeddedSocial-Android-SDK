/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.ui.adapter.renderer;

import android.content.Context;
import android.view.View;

import com.microsoft.socialplus.autorest.models.FollowerStatus;
import com.microsoft.socialplus.account.UserAccount;
import com.microsoft.socialplus.sdk.R;
import com.microsoft.socialplus.server.model.view.UserCompactView;
import com.microsoft.socialplus.ui.adapter.viewholder.UserListItemHolder;

/**
 * Renders users with context menu.
 */
public class UserRenderer extends BaseUserRenderer {

	public UserRenderer(Context context) {
		super(context);
	}

	@Override
	protected void onItemRendered(UserCompactView user, UserListItemHolder holder) {
		super.onItemRendered(user, holder);
		UserAccount userAccount = UserAccount.getInstance();
		if (userAccount.isCurrentUser(user.getHandle()) || user.getFollowerStatus() == FollowerStatus.BLOCKED) {
			holder.actionButton.setVisibility(View.GONE);
		} else {
			holder.actionButton.setVisibility(View.VISIBLE);
			switch (user.getFollowerStatus()) {
				case FOLLOW:
					renderFollowingUser(user, holder);
					break;
				case PENDING:
					renderPendingUser(holder);
					break;
				case NONE:
					renderNotFollowingUser(user, holder);
					break;
			}
		}
	}

	private void renderNotFollowingUser(UserCompactView user, UserListItemHolder holder) {
		holder.actionButton.setEnabled(true);
		holder.actionButton.setOnClickListener(v -> {
			if (UserAccount.getInstance().followUser(user)) {
				if (user.isPrivate()) {
					renderPendingUser(holder);
					user.setFollowerStatus(FollowerStatus.PENDING);
				} else {
					renderFollowingUser(user, holder);
					user.setFollowerStatus(FollowerStatus.FOLLOW);
				}
			}
		});
		holder.actionButton.setText(R.string.sp_follow);
		getStyleHelper().applyGreenStyle(holder.actionButton);
	}

	private void renderPendingUser(UserListItemHolder holder) {
		holder.actionButton.setOnClickListener(null);
		holder.actionButton.setEnabled(false);
		holder.actionButton.setText(R.string.sp_pending);
		getStyleHelper().applyGrayStyle(holder.actionButton);
	}

	private void renderFollowingUser(UserCompactView user, UserListItemHolder holder) {
		holder.actionButton.setOnClickListener(null);
		holder.actionButton.setEnabled(true);
		holder.actionButton.setText(R.string.sp_following);
		holder.actionButton.setOnClickListener(v -> {
			UserAccount.getInstance().unfollowUser(user.getHandle());
			renderNotFollowingUser(user, holder);
			user.setFollowerStatus(FollowerStatus.NONE);
		});
		getStyleHelper().applyGreenCompletedStyle(holder.actionButton);
	}


}
