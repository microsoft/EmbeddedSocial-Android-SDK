/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.ui.adapter.viewholder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.view.View;

import com.microsoft.socialplus.autorest.models.ContentType;
import com.microsoft.socialplus.autorest.models.FollowerStatus;
import com.microsoft.socialplus.account.AuthorizationCause;
import com.microsoft.socialplus.account.UserAccount;
import com.microsoft.socialplus.base.event.EventBus;
import com.microsoft.socialplus.event.ScrollPositionEvent;
import com.microsoft.socialplus.event.click.OpenCommentEvent;
import com.microsoft.socialplus.event.click.ViewCommentCoverImageEvent;
import com.microsoft.socialplus.sdk.R;
import com.microsoft.socialplus.server.model.view.CommentView;
import com.microsoft.socialplus.service.IntentExtras;
import com.microsoft.socialplus.ui.activity.LikesActivity;
import com.microsoft.socialplus.ui.util.ContentUpdateHelper;
import com.microsoft.socialplus.ui.util.menu.CommentContextMenuClickListener;
import com.microsoft.socialplus.ui.util.menu.UserContextMenuHelper;

/**
 * Clock listener for all comment buttons.
 */
public class CommentButtonListener {
	private final Context context;
	private final Container container;

	public CommentButtonListener(Context context, Container container) {
		this.context = context;
		this.container = container;
	}

	public void onClickLike(View view) {
		ContentUpdateHelper.launchLike(
			context,
			(String) view.getTag(R.id.sp_keyHandle),
			ContentType.COMMENT,
			(boolean) view.getTag(R.id.sp_keyIsAdd)
		);
	}

	public void onClickContextMenu(View view) {
		PopupMenu menu = new PopupMenu(context, view);
		if (((Boolean) view.getTag(R.id.sp_keyIsOwnContent))) {
			menu.inflate(R.menu.sp_comment_own);
		} else {
			FollowerStatus userRelationshipStatus = (FollowerStatus) view.getTag(R.id.sp_keyFollowerStatus);
			UserContextMenuHelper.inflateUserRelationshipContextMenu(menu, userRelationshipStatus);
			menu.inflate(R.menu.sp_comment);
		}
		menu.setOnMenuItemClickListener(new CommentContextMenuClickListener(
			context, (CommentView) view.getTag(R.id.sp_keyComment)));
		menu.show();
	}

	public void onClickLikesCount(View view) {
		Intent intent = new Intent(context, LikesActivity.class);
		intent.putExtra(IntentExtras.CONTENT_EXTRA, (String) view.getTag(R.id.sp_keyHandle));
		intent.putExtra(IntentExtras.CONTENT_TYPE, ContentType.COMMENT.toValue());
		context.startActivity(intent);
	}

	public void onClickRepliesCount(View view) {
		if (container == Container.TOPIC) {
			new OpenCommentEvent((CommentView) view.getTag(R.id.sp_keyComment)).submit();
		} else {
			new ScrollPositionEvent((Integer) view.getTag(R.id.sp_keyPosition)).submit();
		}
	}

	public void onClickComment(View view) {
		if (container == Container.TOPIC) {
			new OpenCommentEvent((CommentView) view.getTag(R.id.sp_keyComment), true).submit();
		} else {
			if (UserAccount.getInstance().checkAuthorization(AuthorizationCause.COMMENT)) {
				new ScrollPositionEvent(ScrollPositionEvent.EDIT_POSITION).submit();
			}
		}
	}

	public void onClickContent(View view) {
		if (container == Container.TOPIC) {
			new OpenCommentEvent((CommentView) view.getTag(R.id.sp_keyComment)).submit();
		}
	}

	public void onClickCover(CommentView view) {
		EventBus.post(new ViewCommentCoverImageEvent(view));
	}

	public enum Container {
		TOPIC,
		COMMENT
	}
}
