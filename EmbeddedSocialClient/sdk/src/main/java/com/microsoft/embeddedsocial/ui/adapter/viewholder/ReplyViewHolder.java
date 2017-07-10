/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.adapter.viewholder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.microsoft.embeddedsocial.ui.adapter.QuantityStringUtils;
import com.microsoft.embeddedsocial.ui.util.ButtonStyleHelper;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.server.model.view.ReplyView;
import com.microsoft.embeddedsocial.ui.theme.ThemeAttributes;

/**
 * Init reply view layout.
 */
public class ReplyViewHolder extends UserHeaderViewHolder {

	private final ReplyButtonListener replyButtonListener;
	private final View rootView;

	private TextView replyText;

	private TextView replyLikesCountButton;
	private ImageView likeButton;
	private View dividerLayoutBottom;
	private ButtonStyleHelper buttonStyleHelper;

	public static ReplyViewHolder create(
		ReplyButtonListener replyButtonListener,
		ViewGroup parent,
		HolderType type) {
		View view;
		if (type == HolderType.CONTENT) {
			view = LayoutInflater.from(parent.getContext()).inflate(R.layout.es_layout_reply, parent, false);
		} else {
			view = LayoutInflater.from(parent.getContext()).inflate(R.layout.es_layout_feed_reply, parent, false);
		}
		return new ReplyViewHolder(replyButtonListener, view);
	}

	public ReplyViewHolder(ReplyButtonListener replyButtonListener, View rootView) {
		super(rootView);
		this.replyButtonListener = replyButtonListener;
		this.rootView = rootView;
		this.buttonStyleHelper = new ButtonStyleHelper(rootView.getContext());
		initViews(rootView);
	}

	public void renderItem(ReplyView reply) {
		if (reply == null) {
			return;
		}
		renderUserHeader(reply);

		int actionViewsVisibility = reply.isLocal() ? View.INVISIBLE : View.VISIBLE;
		replyText.setText(reply.getReplyText());

		long totalLikes = reply.getTotalLikes();
		replyLikesCountButton.setText(
				replyLikesCountButton.getResources().getQuantityString(R.plurals.es_topic_likes_pattern,
						QuantityStringUtils.convertLongToInt(totalLikes),
						totalLikes));
		replyLikesCountButton.setTag(R.id.es_keyHandle, reply.getHandle());
		replyLikesCountButton.setVisibility(actionViewsVisibility);
		replyLikesCountButton.setOnClickListener(
				reply.isLocal() ? null : replyButtonListener::onClickLikesCount);

		likeButton.setTag(R.id.es_keyHandle, reply.getHandle());
		likeButton.setTag(R.id.es_keyIsAdd, !reply.isLikeStatus());
		likeButton.setVisibility(actionViewsVisibility);
		buttonStyleHelper.applyAccentColor(likeButton, reply.isLikeStatus());
		likeButton.setOnClickListener(
				reply.isLocal() ? null : replyButtonListener::onClickLike);

		int bgColor = reply.isLocal()
				? ThemeAttributes.getColor(getContext(), R.styleable.es_AppTheme_es_uploadingItemColor)
				: getResources().getColor(R.color.es_reply_background);
		rootView.setBackgroundColor(bgColor);
		contextMenuButton.setTag(R.id.es_keyReply, reply);
	}

	public void renderSingleItem(ReplyView reply) {
		if (reply == null) {
			return;
		}
		renderUserHeader(reply);

		replyText.setText(reply.getReplyText());

		long totalLikes = reply.getTotalLikes();
		replyLikesCountButton.setText(
				replyLikesCountButton.getResources().getQuantityString(R.plurals.es_topic_likes_pattern,
						QuantityStringUtils.convertLongToInt(totalLikes),
						totalLikes));

		dividerLayoutBottom.setVisibility(View.GONE);
		likeButton.setVisibility(View.GONE);

		replyLikesCountButton.setTag(R.id.es_keyHandle, reply.getHandle());
		contextMenuButton.setTag(R.id.es_keyReply, reply);
	}

	private void initViews(View view) {
		setContextMenuClickListener(replyButtonListener::onClickContextMenu);
		setHeaderClickable(true);

		replyText = (TextView) view.findViewById(R.id.es_replyText);
		replyLikesCountButton = (TextView) view.findViewById(R.id.es_replyLikesCountButton);
		buttonStyleHelper.applyAccentColor(replyLikesCountButton);
		likeButton = (ImageView) view.findViewById(R.id.es_likeButton);
		dividerLayoutBottom = view.findViewById(R.id.es_dividerLayoutBottom);
	}
}
