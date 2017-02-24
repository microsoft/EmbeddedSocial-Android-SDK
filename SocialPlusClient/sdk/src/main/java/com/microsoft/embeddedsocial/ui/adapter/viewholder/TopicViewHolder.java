/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.adapter.viewholder;

import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.microsoft.embeddedsocial.ui.util.ContentUpdateHelper;
import com.microsoft.embeddedsocial.autorest.models.PublisherType;
import com.microsoft.embeddedsocial.image.CoverLoader;
import com.microsoft.embeddedsocial.image.ImageViewContentLoader;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.server.model.view.TopicView;
import com.microsoft.embeddedsocial.ui.adapter.QuantityStringUtils;
import com.microsoft.embeddedsocial.ui.theme.ThemeAttributes;
import com.microsoft.embeddedsocial.ui.util.ButtonStyleHelper;

/**
 * Init topic view layout.
 */
public class TopicViewHolder extends UserHeaderViewHolder {

	@Nullable
	private CardView cardRootView;

	private ImageViewContentLoader coverContentLoader;
	protected TextView postTitle;
	private TextView postBody;
	private TextView postLikesCountButton;
	protected LinearLayout postCommentsCountButton;
	private FrameLayout contentButton;
	private ButtonStyleHelper buttonStyleHelper;

	private ImageView likeButton;
	private ImageView commentButton;
	private ImageView pinButton;

	private boolean headerClickable;

	private final TopicButtonsListener topicButtonsListener;

	public static TopicViewHolder create(TopicButtonsListener topicButtonsListener, ViewGroup parent, boolean headerClickable) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.es_layout_card, parent, false);
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
		if (topic.getPublisherType() == PublisherType.APP) {
			headerClickable = false;
			setHeaderClickable(headerClickable);
		}
		postTitle.setText(topic.getTopicTitle());
		ContentUpdateHelper.setTopicBody(getContext(), postBody, topic.getTopicText());

		initLocalTopic(topic);

		long totalLikes = topic.getTotalLikes();
		postLikesCountButton.setText(
				postLikesCountButton.getResources().getQuantityString(R.plurals.es_topic_likes_pattern,
						QuantityStringUtils.convertLongToInt(totalLikes),
						totalLikes));

		long totalComments = topic.getTotalComments();
		TextView postCommentsButtonText = (TextView)postCommentsCountButton.findViewById(R.id.es_postCommentsCountButtonText);
		postCommentsButtonText.setText(
				postCommentsCountButton.getResources().getQuantityString(R.plurals.es_topic_comments_pattern,
						QuantityStringUtils.convertLongToInt(totalComments),
						totalComments));

		postLikesCountButton.setTag(R.id.es_keyHandle, topic.getHandle());
		postCommentsCountButton.setTag(R.id.es_keyTopic, topic);

		contentButton.setTag(R.id.es_keyTopic, topic);
		commentButton.setTag(R.id.es_keyTopic, topic);

		likeButton.setTag(R.id.es_keyHandle, topic.getHandle());
		likeButton.setTag(R.id.es_keyIsAdd, !topic.isLikeStatus());
		buttonStyleHelper.applyAccentColor(likeButton, topic.isLikeStatus());

		pinButton.setTag(R.id.es_keyHandle, topic.getHandle());
		pinButton.setTag(R.id.es_keyIsAdd, !topic.isPinStatus());
		buttonStyleHelper.applyAccentColor(pinButton, topic.isPinStatus());

		ContentUpdateHelper.setTopicCoverImage(coverContentLoader, topic.getImageLocation());
	}

	private void initLocalTopic(TopicView topic) {
		int elementsVisibility;
		if (topic.isLocal()) {
			elementsVisibility = View.GONE;
			setCardBackground(R.styleable.es_AppTheme_es_uploadingItemColor);
		} else {
			elementsVisibility = View.VISIBLE;
			setCardBackground(R.styleable.es_AppTheme_es_cardBackground);
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

		cardRootView = (CardView) view.findViewById(R.id.es_card_root_view);
		ImageView coverImage = (ImageView) view.findViewById(R.id.es_coverImage);
		coverContentLoader = new CoverLoader(coverImage);
		postTitle = (TextView) view.findViewById(R.id.es_postTitle);
		postBody = (TextView) view.findViewById(R.id.es_postBody);

		postLikesCountButton = (TextView) view.findViewById(R.id.es_postLikesCountButton);
		postLikesCountButton.setOnClickListener(topicButtonsListener::onClickLikesCount);
		buttonStyleHelper.applyAccentColor(postLikesCountButton);

		postCommentsCountButton = (LinearLayout) view.findViewById(R.id.es_postCommentsCountButton);
		postCommentsCountButton.setOnClickListener(topicButtonsListener::onClickCommentsCount);
		applyPostCommentsCountButtonAccentColor();

		contentButton = (FrameLayout) view.findViewById(R.id.es_contentButton);
		likeButton = (ImageView) view.findViewById(R.id.es_likeButton);
		commentButton = (ImageView) view.findViewById(R.id.es_commentButton);
		pinButton = (ImageView) view.findViewById(R.id.es_pinButton);

		contentButton.setOnClickListener(topicButtonsListener::onClickContent);
		likeButton.setOnClickListener(topicButtonsListener::onClickLike);
		commentButton.setOnClickListener(topicButtonsListener::onClickComment);
		pinButton.setOnClickListener(topicButtonsListener::onClickPin);
	}

	private void applyPostCommentsCountButtonAccentColor() {
		TextView text = (TextView)postCommentsCountButton.findViewById(R.id.es_postCommentsCountButtonText);
		ImageView image = (ImageView)postCommentsCountButton.findViewById(R.id.es_postCommentsCountButtonImage);
		buttonStyleHelper.applyAccentColor(text);
		buttonStyleHelper.applyAccentColor(image, true);
	}
}
