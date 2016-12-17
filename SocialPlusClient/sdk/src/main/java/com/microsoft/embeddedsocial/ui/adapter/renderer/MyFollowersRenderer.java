/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.adapter.renderer;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.microsoft.embeddedsocial.account.UserAccount;
import com.microsoft.embeddedsocial.data.model.AccountData;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.server.model.view.UserCompactView;
import com.microsoft.embeddedsocial.actions.Action;
import com.microsoft.embeddedsocial.actions.ActionsLauncher;
import com.microsoft.embeddedsocial.autorest.models.FollowerStatus;
import com.microsoft.embeddedsocial.ui.adapter.viewholder.UserListItemHolder;

/**
 * Renders my followers with context menu.
 */
public class MyFollowersRenderer extends UserRenderer {

    public MyFollowersRenderer(Context context) {
        super(context);
    }

    @Override
    protected void onItemRendered(UserCompactView user, UserListItemHolder holder) {
        super.onItemRendered(user, holder);
        UserAccount userAccount = UserAccount.getInstance();
        if (userAccount.isCurrentUser(user.getHandle()) || user.getFollowerStatus() == FollowerStatus.BLOCKED) {
            holder.actionButton.setVisibility(View.GONE);
        } else {
            renderRemoveFollowerButton(userAccount.getAccountDetails(), user, holder);
            renderActionButton(user, holder);
        }
    }

    private void renderRemoveFollowerButton(AccountData currUser, UserCompactView otherUser,
                                            UserListItemHolder holder) {
        TextView button = holder.removeFollowerButton;
        button.setVisibility(View.VISIBLE);
        button.setEnabled(true);
        button.setOnClickListener(v -> {
            Action action = ActionsLauncher.removeFollower(context, otherUser.getHandle());
            if (!action.isFailed()) {
                // redraw the button
                button.setText(R.string.es_removed_follower);
                getStyleHelper().applyRedCompletedStyle(button);
                // decrement local followers count
                currUser.setFollowersCount(Math.max(0, currUser.getFollowersCount() - 1));
            }
        });
        button.setText(R.string.es_remove_follower);
        getStyleHelper().applyRedStyle(button);
    }
}
