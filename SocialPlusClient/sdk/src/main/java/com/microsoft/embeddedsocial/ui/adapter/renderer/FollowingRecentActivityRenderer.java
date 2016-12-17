/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.adapter.renderer;

import android.content.Context;

import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.server.model.view.ActivityView;
import com.microsoft.embeddedsocial.server.model.view.UserCompactView;

/**
 * Renders activity items for following tab.
 */
public class FollowingRecentActivityRenderer extends BaseRecentActivityRenderer {

	private static final int[] LIKE_MESSAGE_IDS = {
		R.string.es_activity_following_like_topic,
		R.string.es_activity_following_like_comment,
		R.string.es_activity_following_like_reply
	};
	private static final int[] MENTION_MESSAGE_IDS = {
		R.string.es_activity_following_mention_topic,
		R.string.es_activity_following_mention_comment,
		R.string.es_activity_following_mention_reply
	};
	private static final int[] CHILD_PEER_MESSAGE_IDS = {
		R.string.es_activity_following_child_peer_topic,
		R.string.es_activity_following_child_peer_comment
	};
	private static final int[] CHILD_MESSAGE_IDS = {
		R.string.es_activity_following_child_topic,
		R.string.es_activity_following_child_comment
	};

	@Override
	protected void renderFollowingEvent(Context context, ActivityView item, ViewHolder holder) {
		UserCompactView user = item.getActorUsers().get(0);
		holder.text.setText(context.getString(R.string.es_activity_following_following, user.getFullName(), item.getActedOnUser().getFullName()));
	}

	@Override
	protected void renderMention(Context context, ActivityView item, ViewHolder holder) {
		UserCompactView user = item.getActorUsers().get(0);
		int messageId = MENTION_MESSAGE_IDS[item.getActedOnContentType().ordinal()];
		holder.text.setText(context.getString(messageId, user.getFullName(), item.getActedOnUser().getFullName(), item.getActedOnContentText()));
	}

	@Override
	protected void renderLike(Context context, ActivityView item, ViewHolder holder) {
		int messageId = LIKE_MESSAGE_IDS[item.getActedOnContentType().ordinal()];
		holder.text.setText(context.getString(messageId, formatActors(context, item), item.getActedOnContentText()));
	}

	@Override
	protected void renderFollowRequest(Context context, ActivityView item, ViewHolder holder) {
		// not supported
	}

	@Override
	protected void renderChildPeer(Context context, ActivityView item, ViewHolder holder) {
		UserCompactView user = item.getActorUsers().get(0);
		int messageId = CHILD_PEER_MESSAGE_IDS[item.getActedOnContentType().ordinal()];
		holder.text.setText(context.getString(messageId, user.getFullName(), item.getActedOnContentText()));
	}

	@Override
	protected void renderChild(Context context, ActivityView item, ViewHolder holder) {
		int messageId = CHILD_MESSAGE_IDS[item.getActedOnContentType().ordinal()];
		holder.text.setText(context.getString(messageId, formatActors(context, item), item.getActedOnContentText()));
	}

	@Override
	protected void renderFollowAccepted(Context context, ActivityView item, ViewHolder holder) {
		// not supported
	}

}
