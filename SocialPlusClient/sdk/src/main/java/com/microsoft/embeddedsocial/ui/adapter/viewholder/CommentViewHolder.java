/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.adapter.viewholder;

import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.microsoft.embeddedsocial.base.utils.ViewUtils;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.server.model.view.CommentView;
import com.microsoft.embeddedsocial.ui.adapter.QuantityStringUtils;
import com.microsoft.embeddedsocial.ui.theme.ThemeAttributes;
import com.microsoft.embeddedsocial.ui.util.ButtonStyleHelper;
import com.microsoft.embeddedsocial.ui.util.ContentUpdateHelper;
import com.microsoft.embeddedsocial.image.CoverLoader;
import com.microsoft.embeddedsocial.image.ImageViewContentLoader;

/**
 * Init comment view layout.
 */
public class CommentViewHolder extends UserHeaderViewHolder {
	private final CommentButtonListener commentButtonListener;

	private FrameLayout coverButton;
	private ImageViewContentLoader coverContentLoader;
	private View commentRootView;
	private TextView commentText;

	private TextView commentLikesCountButton;
	private LinearLayout commentRepliesCountButton;
	private ImageView commentButton;
	private ImageView likeButton;
	private ViewGroup contentButton;
	private ButtonStyleHelper buttonStyleHelper;

	public static CommentViewHolder create(
		CommentButtonListener commentButtonListener,
		ViewGroup parent,
		HolderType holderType) {
		View view;
		if (holderType == HolderType.CONTENT) {
			view = LayoutInflater.from(parent.getContext()).inflate(R.layout.es_layout_comment, parent, false);
		} else {
			view = LayoutInflater.from(parent.getContext()).inflate(R.layout.es_layout_feed_comment, parent, false);
		}
		return new CommentViewHolder(commentButtonListener, view);
	}

	public CommentViewHolder(CommentButtonListener commentButtonListener, View view) {
		super(view);
		this.commentRootView = view.findViewById(R.id.es_comment_root);
		this.commentButtonListener = commentButtonListener;
		this.buttonStyleHelper = new ButtonStyleHelper(view.getContext());
		initViews(view);
	}

	public void renderItem(int position, CommentView comment) {
		if (comment == null) {
			return;
		}
		renderUserHeader(comment.getUser(), comment.getHandle(), comment.getElapsedSeconds());

		commentText.setText(comment.getCommentText());

		long totalLikes = comment.getTotalLikes();
		commentLikesCountButton.setText(
			commentLikesCountButton.getResources().getQuantityString(R.plurals.es_topic_likes_pattern,
					QuantityStringUtils.convertLongToInt(totalLikes),
					totalLikes));

		long totalReplies = comment.getTotalReplies();
		TextView commentButtonText = (TextView)commentRepliesCountButton.findViewById(R.id.es_commentRepliesCountButtonText);
		commentButtonText.setText(
			commentLikesCountButton.getResources().getQuantityString(R.plurals.es_topic_replies_pattern,
					QuantityStringUtils.convertLongToInt(totalReplies),
					totalReplies));

		likeButton.setTag(R.id.es_keyHandle, comment.getHandle());
		likeButton.setTag(R.id.es_keyIsAdd, !comment.isLikeStatus());
		buttonStyleHelper.applyAccentColor(likeButton, comment.isLikeStatus());

		ViewUtils.setVisible(commentButton, !comment.isLocal());
		ViewUtils.setVisible(likeButton, !comment.isLocal());
		int actionViewsVisibility = comment.isLocal() ? View.INVISIBLE : View.VISIBLE;
		commentLikesCountButton.setVisibility(actionViewsVisibility);
		commentRepliesCountButton.setVisibility(actionViewsVisibility);
		contentButton.setVisibility((comment.isLocal() || position == 0) ? View.GONE : View.VISIBLE);
		contentButton.setOnClickListener(comment.isLocal() ? null : commentButtonListener::onClickContent);

		commentLikesCountButton.setTag(R.id.es_keyHandle, comment.getHandle());
		commentRepliesCountButton.setTag(R.id.es_keyHandle, comment.getHandle());
		commentRepliesCountButton.setTag(R.id.es_keyPosition, position);
		commentRepliesCountButton.setTag(R.id.es_keyComment, comment);
		commentButton.setTag(R.id.es_keyHandle, comment.getHandle());
		commentButton.setTag(R.id.es_keyComment, comment);
		contentButton.setTag(R.id.es_keyComment, comment);

		contextMenuButton.setTag(R.id.es_keyComment, comment);

		setCommentBackgroundColor(comment);
		ContentUpdateHelper.setTopicCoverImage(coverContentLoader, comment.getImageLocation());
		setupCoverButton(comment);
	}

	private void setCommentBackgroundColor(CommentView comment) {
		@ColorInt int commentBackground = comment.isLocal()
			? ThemeAttributes.getColor(getContext(), R.styleable.es_AppTheme_es_uploadingItemColor)
			: ContextCompat.getColor(getContext(), R.color.es_comment_background);
		commentRootView.setBackgroundColor(commentBackground);
	}

	public void renderSingleItem(CommentView comment) {
		if (comment == null) {
			return;
		}
		renderUserHeader(comment.getUser(), comment.getHandle(), comment.getElapsedSeconds());
		contextMenuButton.setTag(R.id.es_keyComment, comment);
		commentText.setText(comment.getCommentText());

		long totalLikes = comment.getTotalLikes();
		commentLikesCountButton.setText(
				commentLikesCountButton.getResources().getQuantityString(R.plurals.es_topic_likes_pattern,
						QuantityStringUtils.convertLongToInt(totalLikes),
						totalLikes));
		commentLikesCountButton.setTag(R.id.es_keyHandle, comment.getHandle());

		commentRepliesCountButton.setVisibility(View.GONE);
		likeButton.setVisibility(View.GONE);
		commentButton.setVisibility(View.GONE);
	}

	private void initViews(View view) {
		setContextMenuClickListener(commentButtonListener::onClickContextMenu);
		setHeaderClickable(true);

		contentButton = (ViewGroup) view.findViewById(R.id.es_contentButton);
		ImageView coverImage = (ImageView) view.findViewById(R.id.es_coverImage);
		coverContentLoader = new CoverLoader(coverImage);

		commentText = (TextView) view.findViewById(R.id.es_commentText);
		commentLikesCountButton = (TextView) view.findViewById(R.id.es_commentLikesCountButton);
		buttonStyleHelper.applyAccentColor(commentLikesCountButton);
		commentRepliesCountButton = (LinearLayout) view.findViewById(R.id.es_commentRepliesCountButton);
		applyCommentRepliesCountButtonAccentColor();
		commentButton = (ImageView) view.findViewById(R.id.es_commentButton);
		likeButton = (ImageView) view.findViewById(R.id.es_likeButton);

		commentLikesCountButton.setOnClickListener(commentButtonListener::onClickLikesCount);
		commentRepliesCountButton.setOnClickListener(commentButtonListener::onClickRepliesCount);
		commentButton.setOnClickListener(commentButtonListener::onClickComment);
		likeButton.setOnClickListener(commentButtonListener::onClickLike);
	}

	private void setupCoverButton(CommentView commentView) {
		coverButton = (FrameLayout) commentRootView.findViewById(R.id.es_coverButton);
		if (coverButton != null) {
			coverButton.setOnClickListener((v) ->
					commentButtonListener.onClickCover(commentView));
		}
	}

	private void applyCommentRepliesCountButtonAccentColor() {
		TextView text = (TextView)commentRepliesCountButton.findViewById(R.id.es_commentRepliesCountButtonText);
		ImageView image = (ImageView)commentRepliesCountButton.findViewById(R.id.es_commentRepliesCountButtonImage);
		buttonStyleHelper.applyAccentColor(text);
		buttonStyleHelper.applyAccentColor(image, true);
	}
}
