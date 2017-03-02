/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.microsoft.embeddedsocial.fetcher.FetchersFactory;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.server.model.view.UserCompactView;
import com.microsoft.embeddedsocial.ui.adapter.renderer.UserRenderer;
import com.microsoft.embeddedsocial.ui.fragment.base.BaseUsersListFragment;
import com.microsoft.embeddedsocial.autorest.models.ContentType;
import com.microsoft.embeddedsocial.fetcher.base.Fetcher;
import com.microsoft.embeddedsocial.service.IntentExtras;
import com.microsoft.embeddedsocial.ui.adapter.renderer.Renderer;
import com.microsoft.embeddedsocial.ui.adapter.viewholder.UserListItemHolder;

/**
 * Screen with users who liked a topic.
 */
public class LikesFragment extends BaseUsersListFragment {

	private String contentHandle;
	private ContentType contentType;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		contentHandle = getActivity().getIntent().getStringExtra(IntentExtras.CONTENT_EXTRA);
		contentType = ContentType.fromValue(getActivity().getIntent().getStringExtra(IntentExtras.CONTENT_TYPE));
		if (contentType == null) {
			throw new IllegalArgumentException("Invalid Content Type");
		}
		if (TextUtils.isEmpty(contentHandle)) {
			finishActivity();
		}
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setEmptyDataMessage(R.string.es_message_no_likes);
	}

	@Override
	protected Renderer<? super UserCompactView, ? extends UserListItemHolder> createRenderer() {
		return new UserRenderer(getContext());
	}

	@Override
	protected Fetcher<UserCompactView> createFetcher() {
		return FetchersFactory.createLikeFeedFetcher(contentHandle, contentType);
	}
}
