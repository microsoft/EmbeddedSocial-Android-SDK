/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.fragment;

import android.content.Context;
import android.os.Bundle;

import com.microsoft.embeddedsocial.data.model.TopicFeedType;
import com.microsoft.embeddedsocial.fetcher.FetchersFactory;
import com.microsoft.embeddedsocial.fetcher.base.Fetcher;
import com.microsoft.embeddedsocial.ui.fragment.base.BaseFeedFragment;
import com.microsoft.embeddedsocial.base.utils.EnumUtils;
import com.microsoft.embeddedsocial.server.model.view.TopicView;
import com.microsoft.embeddedsocial.service.IntentExtras;

/**
 * Fragment showing the popular feed.
 */
public class PopularFeedFragment extends BaseFeedFragment {

	private TopicFeedType topicFeedType;

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		topicFeedType = EnumUtils.getValue(getArguments(), IntentExtras.FEED_TYPE, TopicFeedType.class);
	}

	@Override
	protected Fetcher<TopicView> createFetcher() {
		return FetchersFactory.createTopicFeedFetcher(topicFeedType);
	}

	public static PopularFeedFragment createForFeedType(TopicFeedType topicFeedType) {
		PopularFeedFragment fragment = new PopularFeedFragment();
		Bundle arguments = new Bundle();
		EnumUtils.putValue(arguments, IntentExtras.FEED_TYPE, topicFeedType);
		fragment.setArguments(arguments);
		return fragment;
	}
}
