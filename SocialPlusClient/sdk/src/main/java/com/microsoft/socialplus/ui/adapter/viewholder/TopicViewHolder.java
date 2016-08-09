/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.ui.adapter.viewholder;

import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.microsoft.socialplus.data.Preferences;
import com.microsoft.socialplus.image.CoverLoader;
import com.microsoft.socialplus.image.ImageViewContentLoader;
import com.microsoft.socialplus.sdk.R;
import com.microsoft.socialplus.server.model.view.TopicView;
import com.microsoft.socialplus.ui.adapter.QuantityStringUtils;
import com.microsoft.socialplus.ui.theme.ThemeAttributes;
import com.microsoft.socialplus.ui.util.ButtonStyleHelper;
import com.microsoft.socialplus.ui.util.ContentUpdateHelper;

/**
 * Init topic view layout.
 */
public class TopicViewHolder extends UserHeaderViewHolder {

	@Nullable
	private CardView cardRootView;

	private ImageViewContentLoader coverContentLoader;
	protected TextView postTitle;
	private TextView postBody;
	private View appLayout;
	private ImageView postAppIcon;
	private TextView postAppName;
	private TextView postLikesCountButton;
	protected TextView postCommentsCountButton;
	private FrameLayout contentButton;
	private ButtonStyleHelper buttonStyleHelper;

	private ImageView likeButton;
	private ImageView commentButton;
	private ImageView pinButton;

	private final boolean headerClickable;

	private final TopicButtonsListener topicButtonsListener;

	public static TopicViewHolder create(TopicButtonsListener topicButtonsListener, ViewGroup parent, boolean headerClickable) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sp_layout_card, parent, false);
		return new TopicViewHolder(topicButtonsListener, view, headerClickable);
	}

	protected TopicViewHolder(TopicButtonsListener topicButtonsListener, View view, boolean headerClickable) {
		super(view);
		this.topicButtonsListener = topicButtonsListener;
		this.headerClickable = headerClickable;
		this.buttonStyleHelper = new ButtonStyleHelper(view.getContext());
		initViews(view);
	}

	public void renderItem(int position, TopicView topic) {
		renderUserHeader(topic);

		postTitle.setText(topic.getTopicTitle());
		ContentUpdateHelper.setTopicBody(getContext(), postBody, topic.getTopicText());

		if (!Preferences.getInstance().isDisplayApp() || topic.getApp() == null) {
			appLayout.setVisibility(View.GONE);
		} else {
			appLayout.setVisibility(View.VISIBLE);
			postAppName.setText(topic.getApp().getAppName());
			ContentUpdateHelper.setTopicAppIcon(postAppIcon, topic.getApp().getAppIconUrl());
		}

		initLocalTopic(topic);

		long totalLikes = topic.getTotalLikes();
		postLikesCountButton.setText(
				postLikesCountButton.getResources().getQuantityString(R.plurals.sp_topic_likes_pattern,
						QuantityStringUtils.convertLongToInt(totalLikes),
						totalLikes));

		long totalComments = topic.getTotalComments();
		postCommentsCountButton.setText(
				postCommentsCountButton.getResources().getQuantityString(R.plurals.sp_topic_comments_pattern,
						QuantityStringUtils.convertLongToInt(totalComments),
						totalComments));

		postLikesCountButton.setTag(R.id.sp_keyHandle, topic.getHandle());
		postCommentsCountButton.setTag(R.id.sp_keyTopic, topic);

		contentButton.setTag(R.id.sp_keyTopic, topic);
		commentButton.setTag(R.id.sp_keyTopic, topic);

		likeButton.setTag(R.id.sp_keyHandle, topic.getHandle());
		likeButton.setTag(R.id.sp_keyIsAdd, !topic.isLikeStatus());
		buttonStyleHelper.applyAccentColor(likeButton, topic.isLikeStatus());

		pinButton.setTag(R.id.sp_keyHandle, topic.getHandle());
		pinButton.setTag(R.id.sp_keyIsAdd, !topic.isPinStatus());
		buttonStyleHelper.applyAccentColor(pinButton, topic.isPinStatus());

		ContentUpdateHelper.setTopicCoverImage(coverContentLoader, topic.getImageLocation());
	}

	private void initLocalTopic(TopicView topic) {
		int elementsVisibility;
		if (topic.isLocal()) {
			elementsVisibility = View.GONE;
			setCardBackground(R.styleable.sp_AppTheme_sp_uploadingItemColor);
		} else {
			elementsVisibility = View.VISIBLE;
			setCardBackground(R.styleable.sp_AppTheme_sp_cardBackground);
		}

		View[] topicElements = {pinButton, likeButton, commentButton, elapsedTime};
		for (View button : topicElements) {
			button.setVisibility(elementsVisibility);
		}
	}

	private void setCardBackground(int colorAttrId) {
		if (cardRootView != null) {
			int color = ThemeAttributes.getColor(getContext(), colorAttrId);
			cardRootView.setCardBackgroundColor(color);
		}
	}

	private void initViews(View view) {
		setContextMenuClickListener(topicButtonsListener::onClickContextMenu);
		setHeaderClickable(headerClickable);

		cardRootView = (CardView) view.findViewById(R.id.sp_card_root_view);
		ImageView coverImage = (ImageView) view.findViewById(R.id.sp_coverImage);
		coverContentLoader = new CoverLoader(coverImage);
		postTitle = (TextView) view.findViewById(R.id.sp_postTitle);
		postBody = (TextView) view.findViewById(R.id.sp_postBody);
		appLayout = view.findViewById(R.id.sp_postAppLayout);

		postAppName = (TextView) view.findViewById(R.id.sp_postAppName);
		postAppIcon = (ImageView) view.findViewById(R.id.sp_postAppIcon);

		postLikesCountButton = (TextView) view.findViewById(R.id.sp_postLikesCountButton);
		postLikesCountButton.setOnClickListener(topicButtonsListener::onClickLikesCount);
		buttonStyleHelper.applyAccentColor(postLikesCountButton);

		postCommentsCountButton = (TextView) view.findViewById(R.id.sp_postCommentsCountButton);
		postCommentsCountButton.setOnClickListener(topicButtonsListener::onClickCommentsCount);
		buttonStyleHelper.applyAccentColor(postCommentsCountButton);

		contentButton = (FrameLayout) view.findViewById(R.id.sp_contentButton);
		likeButton = (ImageView) view.findViewById(R.id.sp_likeButton);
		commentButton = (ImageView) view.findViewById(R.id.sp_commentButton);
		pinButton = (ImageView) view.findViewById(R.id.sp_pinButton);

		contentButton.setOnClickListener(topicButtonsListener::onClickContent);
		likeButton.setOnClickListener(topicButtonsListener::onClickLike);
		commentButton.setOnClickListener(topicButtonsListener::onClickComment);
		pinButton.setOnClickListener(topicButtonsListener::onClickPin);
	}
}
