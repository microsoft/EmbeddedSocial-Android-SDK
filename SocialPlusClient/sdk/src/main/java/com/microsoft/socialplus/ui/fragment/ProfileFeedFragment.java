/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.microsoft.socialplus.base.utils.EnumUtils;
import com.microsoft.socialplus.data.model.TopicFeedType;
import com.microsoft.socialplus.fetcher.FetchersFactory;
import com.microsoft.socialplus.fetcher.base.Fetcher;
import com.microsoft.socialplus.server.model.view.TopicView;
import com.microsoft.socialplus.service.IntentExtras;
import com.microsoft.socialplus.ui.adapter.renderer.CardViewRenderer;
import com.microsoft.socialplus.ui.adapter.renderer.Renderer;
import com.microsoft.socialplus.ui.adapter.viewholder.TopicRenderOptions;
import com.microsoft.socialplus.ui.fragment.base.BaseFeedFragment;

/**
 * Shows user's feed.
 */
public class ProfileFeedFragment extends BaseFeedFragment {

	private String userHandle;
	private TopicFeedType topicFeedType;

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		Bundle arguments = getArguments();
		userHandle = arguments.getString(IntentExtras.USER_HANDLE);
		topicFeedType = EnumUtils.getValue(arguments, IntentExtras.FEED_TYPE, TopicFeedType.class);
	}

	@Override
	protected Fetcher<TopicView> createFetcher() {
		return FetchersFactory.createProfileFeedFetcher(userHandle, topicFeedType);
	}

	@Override
	protected Renderer<TopicView, ? extends RecyclerView.ViewHolder> createCardRenderer() {
		TopicRenderOptions options = new TopicRenderOptions();
		options.setHeaderClickable(false);
		return new CardViewRenderer(getContext(), options);
	}

	@Override
	protected boolean canContainLocalPosts() {
		return topicFeedType == TopicFeedType.USER_RECENT;
	}

	public static ProfileFeedFragment create(String userHandle, TopicFeedType topicFeedType) {
		ProfileFeedFragment fragment = new ProfileFeedFragment();
		Bundle arguments = new Bundle();
		arguments.putString(IntentExtras.USER_HANDLE, userHandle);
		EnumUtils.putValue(arguments, IntentExtras.FEED_TYPE, topicFeedType);
		fragment.setArguments(arguments);
		return fragment;
	}

}
