/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.ui.fragment;

import com.microsoft.socialplus.fetcher.FetchersFactory;
import com.microsoft.socialplus.fetcher.base.Fetcher;
import com.microsoft.socialplus.server.model.view.TopicView;
import com.microsoft.socialplus.ui.fragment.base.BaseFeedFragment;

/**
 * Shows pinned topics.
 */
public class PinsFragment extends BaseFeedFragment {

	@Override
	protected Fetcher<TopicView> createFetcher() {
		return FetchersFactory.createPinsFeedFetcher();
	}

}
