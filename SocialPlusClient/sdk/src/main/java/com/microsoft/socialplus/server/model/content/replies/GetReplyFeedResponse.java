/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.server.model.content.replies;

import com.microsoft.socialplus.autorest.models.FeedResponseReplyView;
import com.microsoft.socialplus.server.model.FeedUserResponse;
import com.microsoft.socialplus.server.model.ListResponse;
import com.microsoft.socialplus.server.model.view.ReplyView;

import java.util.ArrayList;
import java.util.List;

public class GetReplyFeedResponse extends FeedUserResponse implements ListResponse<ReplyView> {

	private List<ReplyView> replies;

	public GetReplyFeedResponse(List<ReplyView> replies) {
		this.replies = replies;
	}

	public GetReplyFeedResponse(FeedResponseReplyView response) {
		replies = new ArrayList<>();
		for (com.microsoft.socialplus.autorest.models.ReplyView reply : response.getData()) {
			replies.add(new ReplyView(reply));
		}
	}

	@Override
	public List<ReplyView> getData() {
		return replies;
	}
}
