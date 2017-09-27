/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.adapter.renderer;

import android.content.Context;
import android.view.View;

import com.microsoft.embeddedsocial.account.UserAccount;
import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.sdk.Options;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.server.model.view.UserCompactView;
import com.microsoft.embeddedsocial.autorest.models.FollowerStatus;
import com.microsoft.embeddedsocial.ui.adapter.viewholder.UserListItemHolder;

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
		} else if (GlobalObjectRegistry.getObject(Options.class).userRelationsEnabled()){
			// only render the action button if user relations are enabled
			renderActionButton(user, holder);
		}
	}

	protected void renderActionButton(UserCompactView user, UserListItemHolder holder) {
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
		holder.actionButton.setText(R.string.es_follow);
		getStyleHelper().applyGreenStyle(holder.actionButton);
	}

	private void renderPendingUser(UserListItemHolder holder) {
		holder.actionButton.setOnClickListener(null);
		holder.actionButton.setEnabled(false);
		holder.actionButton.setText(R.string.es_pending);
		getStyleHelper().applyGrayStyle(holder.actionButton);
	}

	private void renderFollowingUser(UserCompactView user, UserListItemHolder holder) {
		holder.actionButton.setOnClickListener(null);
		holder.actionButton.setEnabled(true);
		holder.actionButton.setText(R.string.es_following);
		holder.actionButton.setOnClickListener(v -> {
			UserAccount.getInstance().unfollowUser(user.getHandle());
			renderNotFollowingUser(user, holder);
			user.setFollowerStatus(FollowerStatus.NONE);
		});
		getStyleHelper().applyGreenCompletedStyle(holder.actionButton);
	}
}
