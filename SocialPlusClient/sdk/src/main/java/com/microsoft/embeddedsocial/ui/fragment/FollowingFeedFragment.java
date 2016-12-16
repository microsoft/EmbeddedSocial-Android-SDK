/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.fragment;

import android.support.v7.widget.RecyclerView;

import com.microsoft.embeddedsocial.data.model.TopicFeedType;
import com.microsoft.embeddedsocial.fetcher.FetchersFactory;
import com.microsoft.embeddedsocial.fetcher.base.Fetcher;
import com.microsoft.embeddedsocial.ui.adapter.renderer.CardViewRenderer;
import com.microsoft.embeddedsocial.ui.adapter.renderer.Renderer;
import com.microsoft.embeddedsocial.ui.adapter.viewholder.TopicRenderOptions;
import com.microsoft.embeddedsocial.ui.fragment.base.BaseFeedFragment;
import com.microsoft.embeddedsocial.server.model.view.TopicView;

/**
 * Fragment showing the following feed.
 */
public class FollowingFeedFragment extends BaseFeedFragment {

	@Override
	protected Fetcher<TopicView> createFetcher() {
		return FetchersFactory.createTopicFeedFetcher(TopicFeedType.FOLLOWING_RECENT);
	}

	@Override
	protected Renderer<TopicView, ? extends RecyclerView.ViewHolder> createCardRenderer() {
		TopicRenderOptions topicRenderOptions = new TopicRenderOptions();
		topicRenderOptions.setShouldShowHideTopicItem(true);
		return new CardViewRenderer(getContext(), topicRenderOptions);
	}

	@Override
	protected boolean canContainLocalPosts() {
		return true;
	}
}
