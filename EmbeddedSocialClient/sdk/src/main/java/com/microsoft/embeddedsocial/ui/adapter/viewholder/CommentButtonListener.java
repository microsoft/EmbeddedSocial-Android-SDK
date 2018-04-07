/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.adapter.viewholder;

import com.microsoft.embeddedsocial.account.AuthorizationCause;
import com.microsoft.embeddedsocial.account.UserAccount;
import com.microsoft.embeddedsocial.autorest.models.ContentType;
import com.microsoft.embeddedsocial.autorest.models.FollowerStatus;
import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.base.event.EventBus;
import com.microsoft.embeddedsocial.event.ScrollPositionEvent;
import com.microsoft.embeddedsocial.event.click.OpenCommentEvent;
import com.microsoft.embeddedsocial.event.click.ViewCommentCoverImageEvent;
import com.microsoft.embeddedsocial.sdk.Options;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.server.model.view.CommentView;
import com.microsoft.embeddedsocial.service.IntentExtras;
import com.microsoft.embeddedsocial.ui.activity.LikesActivity;
import com.microsoft.embeddedsocial.ui.util.ContentUpdateHelper;
import com.microsoft.embeddedsocial.ui.util.menu.CommentContextMenuClickListener;
import com.microsoft.embeddedsocial.ui.util.menu.UserContextMenuHelper;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.view.View;

/**
 * Clock listener for all comment buttons.
 */
public class CommentButtonListener {
    private final Fragment fragment;
    private final Context context;
    private final Container container;

    public CommentButtonListener(Fragment fragment, Container container) {
        this.fragment = fragment;
        this.context = fragment.getContext();
        this.container = container;
    }

    public void onClickLike(View view) {
        ContentUpdateHelper.launchLike(
            fragment,
            (String) view.getTag(R.id.es_keyHandle),
            ContentType.COMMENT,
            (boolean) view.getTag(R.id.es_keyIsAdd)
        );
    }

    public void onClickContextMenu(View view) {
        PopupMenu menu = new PopupMenu(context, view);
        if (((Boolean) view.getTag(R.id.es_keyIsOwnContent))) {
            menu.inflate(R.menu.es_comment_own);
        } else {
            FollowerStatus userRelationshipStatus = (FollowerStatus) view.getTag(R.id.es_keyFollowerStatus);
            if (GlobalObjectRegistry.getObject(Options.class).userRelationsEnabled()) {
                // only inflate the user relationship options if user relations are enabled
                UserContextMenuHelper.inflateUserRelationshipContextMenu(menu, userRelationshipStatus);
            }
            menu.inflate(R.menu.es_comment);
        }
        menu.setOnMenuItemClickListener(new CommentContextMenuClickListener(
            fragment, (CommentView) view.getTag(R.id.es_keyComment)));
        menu.show();
    }

    public void onClickLikesCount(View view) {
        Intent intent = new Intent(context, LikesActivity.class);
        intent.putExtra(IntentExtras.CONTENT_EXTRA, (String) view.getTag(R.id.es_keyHandle));
        intent.putExtra(IntentExtras.CONTENT_TYPE, ContentType.COMMENT.toValue());
        context.startActivity(intent);
    }

    public void onClickRepliesCount(View view) {
        if (container == Container.TOPIC) {
            new OpenCommentEvent(fragment, (CommentView) view.getTag(R.id.es_keyComment)).submit();
        } else {
            new ScrollPositionEvent((Integer) view.getTag(R.id.es_keyPosition)).submit();
        }
    }

    public void onClickComment(View view) {
        if (container == Container.TOPIC) {
            new OpenCommentEvent(fragment, (CommentView) view.getTag(R.id.es_keyComment), true).submit();
        } else {
            if (UserAccount.getInstance().checkAuthorization(fragment, AuthorizationCause.COMMENT)) {
                new ScrollPositionEvent(ScrollPositionEvent.EDIT_POSITION).submit();
            }
        }
    }

    public void onClickContent(View view) {
        if (container == Container.TOPIC) {
            new OpenCommentEvent(fragment, (CommentView) view.getTag(R.id.es_keyComment)).submit();
        }
    }

    public void onClickCover(CommentView view) {
        EventBus.post(new ViewCommentCoverImageEvent(fragment, view));
    }

    public enum Container {
        TOPIC,
        COMMENT
    }
}
