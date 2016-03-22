/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.ui.adapter.viewholder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.microsoft.socialplus.sdk.R;
import com.microsoft.socialplus.server.model.view.ReplyView;
import com.microsoft.socialplus.ui.adapter.QuantityStringUtils;
import com.microsoft.socialplus.ui.theme.ThemeAttributes;
import com.microsoft.socialplus.ui.util.ButtonStyleHelper;

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
			view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sp_layout_reply, parent, false);
		} else {
			view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sp_layout_feed_reply, parent, false);
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
				replyLikesCountButton.getResources().getQuantityString(R.plurals.sp_topic_likes_pattern,
						QuantityStringUtils.convertLongToInt(totalLikes),
						totalLikes));
		replyLikesCountButton.setTag(R.id.sp_keyHandle, reply.getHandle());
		replyLikesCountButton.setVisibility(actionViewsVisibility);
		replyLikesCountButton.setOnClickListener(
				reply.isLocal() ? null : replyButtonListener::onClickLikesCount);

		likeButton.setTag(R.id.sp_keyHandle, reply.getHandle());
		likeButton.setTag(R.id.sp_keyIsAdd, !reply.isLikeStatus());
		likeButton.setVisibility(actionViewsVisibility);
		buttonStyleHelper.applyAccentColor(likeButton, reply.isLikeStatus());
		likeButton.setOnClickListener(
				reply.isLocal() ? null : replyButtonListener::onClickLike);

		int bgColor = reply.isLocal()
				? ThemeAttributes.getColor(getContext(), R.styleable.sp_AppTheme_sp_uploadingItemColor)
				: getResources().getColor(R.color.sp_reply_background);
		rootView.setBackgroundColor(bgColor);
		contextMenuButton.setTag(R.id.sp_keyReply, reply);
	}

	public void renderSingleItem(ReplyView reply) {
		if (reply == null) {
			return;
		}
		renderUserHeader(reply);

		replyText.setText(reply.getReplyText());

		long totalLikes = reply.getTotalLikes();
		replyLikesCountButton.setText(
				replyLikesCountButton.getResources().getQuantityString(R.plurals.sp_topic_likes_pattern,
						QuantityStringUtils.convertLongToInt(totalLikes),
						totalLikes));

		dividerLayoutBottom.setVisibility(View.GONE);
		likeButton.setVisibility(View.GONE);

		replyLikesCountButton.setTag(R.id.sp_keyHandle, reply.getHandle());
		contextMenuButton.setTag(R.id.sp_keyReply, reply);
	}

	private void initViews(View view) {
		setContextMenuClickListener(replyButtonListener::onClickContextMenu);
		setHeaderClickable(true);

		replyText = (TextView) view.findViewById(R.id.sp_replyText);
		replyLikesCountButton = (TextView) view.findViewById(R.id.sp_replyLikesCountButton);
		buttonStyleHelper.applyAccentColor(replyLikesCountButton);
		likeButton = (ImageView) view.findViewById(R.id.sp_likeButton);
		dividerLayoutBottom = view.findViewById(R.id.sp_dividerLayoutBottom);
	}
}
