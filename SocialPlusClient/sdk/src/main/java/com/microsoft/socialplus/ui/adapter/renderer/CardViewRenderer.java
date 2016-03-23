/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.ui.adapter.renderer;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.microsoft.socialplus.autorest.models.ContentType;
import com.microsoft.socialplus.base.event.EventBus;
import com.microsoft.socialplus.event.click.OpenTopicEvent;
import com.microsoft.socialplus.sdk.R;
import com.microsoft.socialplus.server.model.view.TopicView;
import com.microsoft.socialplus.ui.adapter.viewholder.TopicButtonsListener;
import com.microsoft.socialplus.ui.adapter.viewholder.TopicRenderOptions;
import com.microsoft.socialplus.ui.adapter.viewholder.TopicViewHolder;
import com.microsoft.socialplus.ui.util.ContentUpdateHelper;
import com.microsoft.socialplus.ui.util.menu.TopicContextMenu;

/**
 * Renders a topic as CardView.
 */
public class CardViewRenderer extends Renderer<TopicView, TopicViewHolder> {

	private final Context context;
	private final CardViewTopicButtonsListener topicButtonsListener;
	private final TopicRenderOptions options;

	public CardViewRenderer(Context context) {
		this(context, TopicRenderOptions.getDefault());
	}

	public CardViewRenderer(Context context, TopicRenderOptions options) {
		this.context = context;
		this.topicButtonsListener = new CardViewTopicButtonsListener(context);
		this.options = options;
	}

	private void showTopicContextMenu(View anchorView) {
		PopupMenu menu = new PopupMenu(context, anchorView);
		TopicView topic = (TopicView) anchorView.getTag(R.id.sp_keyTopic);
		TopicContextMenu.inflateContextMenu(context, menu, topic, options);
		menu.show();
	}

	private void openTopic(TopicView topic, boolean scrollDown) {
		EventBus.post(new OpenTopicEvent(topic, scrollDown));
	}

	@Override
	public TopicViewHolder createViewHolder(ViewGroup parent) {
		return TopicViewHolder.create(topicButtonsListener, parent, options.isHeaderClickable());
	}

	@Override
	protected void onItemRendered(TopicView topic, TopicViewHolder holder) {
		holder.renderItem(RecyclerView.NO_POSITION, topic);
	}

	private class CardViewTopicButtonsListener extends TopicButtonsListener {

		public CardViewTopicButtonsListener(Context context) {
			super(context);
		}

		@Override
		public void onClickContent(View view) {
			openTopic((TopicView) view.getTag(R.id.sp_keyTopic), false);
		}

		@Override
		public void onClickCover(View view) {
			// Not used
		}

		@Override
		public void onClickContextMenu(View view) {
			showTopicContextMenu(view);
		}

		@Override
		public void onClickCommentsCount(View view) {
			openTopic((TopicView) view.getTag(R.id.sp_keyTopic), false);
		}

		@Override
		public void onClickComment(View view) {
			openTopic((TopicView) view.getTag(R.id.sp_keyTopic), true);
		}

		@Override
		public void onClickPin(View view) {
			ContentUpdateHelper.launchPin(
				context,
				(String) view.getTag(R.id.sp_keyHandle),
				(boolean) view.getTag(R.id.sp_keyIsAdd)
			);
		}

		@Override
		public void onClickLike(View view) {
			ContentUpdateHelper.launchLike(
				context,
				(String) view.getTag(R.id.sp_keyHandle),
				ContentType.TOPIC,
				(boolean) view.getTag(R.id.sp_keyIsAdd)
			);
		}

	}
}
