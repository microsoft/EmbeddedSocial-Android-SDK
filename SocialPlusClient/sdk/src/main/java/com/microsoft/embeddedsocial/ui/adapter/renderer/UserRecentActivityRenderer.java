/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.adapter.renderer;

import android.content.Context;

import com.microsoft.embeddedsocial.server.model.view.ActivityView;
import com.microsoft.embeddedsocial.server.model.view.UserCompactView;
import com.microsoft.embeddedsocial.autorest.models.ContentType;
import com.microsoft.embeddedsocial.sdk.R;

/**
 * Renders activity items foe user's tab.
 */
public class UserRecentActivityRenderer extends BaseRecentActivityRenderer {

	private static final int[] LIKE_MESSAGE_IDS = {
		R.string.es_activity_you_like_topic,
		R.string.es_activity_you_like_comment,
		R.string.es_activity_you_like_reply
	};
	private static final int[] MENTION_MESSAGE_IDS = {
		R.string.es_activity_you_mention_topic,
		R.string.es_activity_you_mention_comment,
		R.string.es_activity_you_mention_reply
	};
	private static final int[] CHILD_PEER_MESSAGE_IDS = {
		R.string.es_activity_you_child_peer_topic,
		R.string.es_activity_you_child_peer_comment
	};
	private static final int[] CHILD_MESSAGE_IDS = {
		R.string.es_activity_you_child_topic,
		R.string.es_activity_you_child_comment
	};

	/**
	 * @param actedOnContentType type of content acted on
	 * @return 0 for topic, 1 for comment, 2 for reply
	 */
	private static int getMessageIdIndex(ContentType actedOnContentType) {
		switch (actedOnContentType) {
			case TOPIC:
				return 0;
			case COMMENT:
				return 1;
			default:
				return 2;
		}
	}

	@Override
	protected void renderFollowingEvent(Context context, ActivityView item, ViewHolder holder) {
		UserCompactView user = item.getActorUsers().get(0);
		holder.text.setText(context.getString(R.string.es_activity_you_following, user.getFullName()));
	}

	@Override
	protected void renderMention(Context context, ActivityView item, ViewHolder holder) {
		UserCompactView user = item.getActorUsers().get(0);
		int messageId = MENTION_MESSAGE_IDS[getMessageIdIndex(item.getActedOnContentType())];
		holder.text.setText(context.getString(messageId, user.getFullName(), item.getActedOnContentText()));
	}

	@Override
	protected void renderLike(Context context, ActivityView item, ViewHolder holder) {
		int messageId = LIKE_MESSAGE_IDS[getMessageIdIndex(item.getActedOnContentType())];
		holder.text.setText(context.getString(messageId, formatActors(context, item), item.getActedOnContentText()));
	}

	@Override
	protected void renderFollowRequest(Context context, ActivityView item, ViewHolder holder) {
		UserCompactView user = item.getActorUsers().get(0);
		holder.text.setText(context.getString(R.string.es_activity_you_follow_request, user.getFullName()));
	}

	@Override
	protected void renderChildPeer(Context context, ActivityView item, ViewHolder holder) {
		UserCompactView user = item.getActorUsers().get(0);
		int messageId = CHILD_PEER_MESSAGE_IDS[getMessageIdIndex(item.getActedOnContentType())];
		holder.text.setText(context.getString(messageId, user.getFullName(), item.getActedOnContentText()));
	}

	@Override
	protected void renderChild(Context context, ActivityView item, ViewHolder holder) {
		int messageId = CHILD_MESSAGE_IDS[getMessageIdIndex(item.getActedOnContentType())];
		holder.text.setText(context.getString(messageId, formatActors(context, item), item.getActedOnContentText()));
	}

	@Override
	protected void renderFollowAccepted(Context context, ActivityView item, ViewHolder holder) {
		UserCompactView user = item.getActorUsers().get(0);
		holder.text.setText(context.getString(R.string.es_activity_you_follow_accepted, user.getFullName()));
	}

}
