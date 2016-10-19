/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.ui.fragment;

import android.support.v7.widget.RecyclerView;

import com.microsoft.socialplus.data.model.TopicFeedType;
import com.microsoft.socialplus.fetcher.FetchersFactory;
import com.microsoft.socialplus.fetcher.base.Fetcher;
import com.microsoft.socialplus.server.model.view.TopicView;
import com.microsoft.socialplus.ui.adapter.renderer.CardViewRenderer;
import com.microsoft.socialplus.ui.adapter.renderer.Renderer;
import com.microsoft.socialplus.ui.adapter.viewholder.TopicRenderOptions;
import com.microsoft.socialplus.ui.fragment.base.BaseFeedFragment;

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
