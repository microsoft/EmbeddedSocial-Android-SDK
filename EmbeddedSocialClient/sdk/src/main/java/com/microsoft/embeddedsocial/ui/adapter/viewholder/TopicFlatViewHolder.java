/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.adapter.viewholder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.server.model.view.TopicView;

/**
 * Init topic flat view layout.
 */
public class TopicFlatViewHolder extends TopicViewHolder {
	private View topDivider;
	private View bottomDivider;
	private FrameLayout contentButton;
	private FrameLayout coverButton;

	public static TopicFlatViewHolder create(TopicButtonsListener topicButtonsListener, ViewGroup parent) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.es_layout_topic, parent, false);
		return new TopicFlatViewHolder(topicButtonsListener, view);
	}

	protected TopicFlatViewHolder(TopicButtonsListener topicButtonsListener, View view) {
		super(topicButtonsListener, view, true);
		topDivider = view.findViewById(R.id.es_dividerLayoutTop);
		bottomDivider = view.findViewById(R.id.es_dividerLayoutBottom);
		contentButton = (FrameLayout) view.findViewById(R.id.es_contentButton);
		coverButton = (FrameLayout) view.findViewById(R.id.es_coverButton);
		coverButton.setOnClickListener(topicButtonsListener::onClickCover);
	}

	@Override
	public void renderItem(int position, TopicView topic) {
		if (topic == null) {
			return;
		}
		super.renderItem(position, topic);
		postCommentsCountButton.setTag(R.id.es_keyPosition, position);
		topDivider.setVisibility(View.GONE);
		bottomDivider.setVisibility(View.VISIBLE);
		contentButton.setVisibility(View.GONE);
		coverButton.setVisibility(View.VISIBLE);
		postTitle.setSingleLine(false);

		coverButton.setTag(R.id.es_keyTopic, topic);
	}
}
