/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.adapter.viewholder;

/**
 * Options for a topic renderer.
 */
public class TopicRenderOptions {

	private boolean shouldShowHideTopicItem = false;
	private boolean headerClickable = true;

	public boolean shouldShowHideTopicItem() {
		return shouldShowHideTopicItem;
	}

	public void setShouldShowHideTopicItem(boolean shouldShowHideTopicItem) {
		this.shouldShowHideTopicItem = shouldShowHideTopicItem;
	}

	public static TopicRenderOptions getDefault() {
		return new TopicRenderOptions();
	}

	public boolean isHeaderClickable() {
		return headerClickable;
	}

	public void setHeaderClickable(boolean headerClickable) {
		this.headerClickable = headerClickable;
	}
}
