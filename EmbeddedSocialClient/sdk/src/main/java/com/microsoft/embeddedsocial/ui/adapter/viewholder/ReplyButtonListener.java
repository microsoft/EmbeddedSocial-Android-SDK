/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.adapter.viewholder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.view.View;

import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.sdk.Options;
import com.microsoft.embeddedsocial.ui.util.ContentUpdateHelper;
import com.microsoft.embeddedsocial.autorest.models.ContentType;
import com.microsoft.embeddedsocial.autorest.models.FollowerStatus;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.server.model.view.ReplyView;
import com.microsoft.embeddedsocial.service.IntentExtras;
import com.microsoft.embeddedsocial.ui.activity.LikesActivity;
import com.microsoft.embeddedsocial.ui.util.menu.ReplyContextMenuClickListener;
import com.microsoft.embeddedsocial.ui.util.menu.UserContextMenuHelper;

/**
 * Click listener for all reply buttons.
 */
public class ReplyButtonListener {
	private Context context;

	public ReplyButtonListener(Context context) {
		this.context = context;
	}

	public void onClickLike(View view) {
		ContentUpdateHelper.launchLike(
			context,
			(String) view.getTag(R.id.es_keyHandle),
			ContentType.REPLY,
			(boolean) view.getTag(R.id.es_keyIsAdd)
		);
	}

	public void onClickContextMenu(View view) {
		PopupMenu menu = new PopupMenu(context, view);
		if (((Boolean) view.getTag(R.id.es_keyIsOwnContent))) {
			menu.inflate(R.menu.es_reply_own);
		} else {
			FollowerStatus userRelationshipStatus = (FollowerStatus) view.getTag(R.id.es_keyFollowerStatus);
			if (GlobalObjectRegistry.getObject(Options.class).userRelationsEnabled()) {
				// only inflate the user relationship options if user relations are enabled
				UserContextMenuHelper.inflateUserRelationshipContextMenu(menu, userRelationshipStatus);
			}
			menu.inflate(R.menu.es_reply);
		}
		menu.setOnMenuItemClickListener(new ReplyContextMenuClickListener(
			context, (ReplyView) view.getTag(R.id.es_keyReply)));
		menu.show();
	}

	public void onClickLikesCount(View view) {
		Intent intent = new Intent(context, LikesActivity.class);
		intent.putExtra(IntentExtras.CONTENT_EXTRA, (String) view.getTag(R.id.es_keyHandle));
		intent.putExtra(IntentExtras.CONTENT_TYPE, ContentType.REPLY.toValue());

		context.startActivity(intent);
	}
}
