/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model.content.replies;

import com.microsoft.embeddedsocial.server.model.ListResponse;
import com.microsoft.embeddedsocial.autorest.models.FeedResponseReplyView;
import com.microsoft.embeddedsocial.server.model.FeedUserResponse;
import com.microsoft.embeddedsocial.server.model.view.ReplyView;

import java.util.ArrayList;
import java.util.List;

public class GetReplyFeedResponse extends FeedUserResponse implements ListResponse<ReplyView> {

	private List<ReplyView> replies;

	public GetReplyFeedResponse(List<ReplyView> replies) {
		this.replies = replies;
	}

	public GetReplyFeedResponse(FeedResponseReplyView response) {
		replies = new ArrayList<>();
		for (com.microsoft.embeddedsocial.autorest.models.ReplyView reply : response.getData()) {
			replies.add(new ReplyView(reply));
		}
	}

	@Override
	public List<ReplyView> getData() {
		return replies;
	}
}
