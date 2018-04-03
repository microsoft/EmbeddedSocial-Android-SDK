/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.event.click;

import com.microsoft.embeddedsocial.base.event.AbstractEvent;
import com.microsoft.embeddedsocial.base.event.HandlingThread;
import com.microsoft.embeddedsocial.base.event.ThreadType;

/**
 * Trending hashtag was selected from the list event.
 */
@HandlingThread(ThreadType.CALLING_MAIN)
public class OnTrendingHashtagSelectedEvent extends AbstractEvent {
	private final String trendingHashtag;

	public OnTrendingHashtagSelectedEvent(String trendingHashtag) {
		this.trendingHashtag = trendingHashtag;
	}

	public String getTrendingHashtag() {
		return trendingHashtag;
	}
}
