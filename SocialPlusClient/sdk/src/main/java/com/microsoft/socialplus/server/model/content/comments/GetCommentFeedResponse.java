/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.server.model.content.comments;

import com.microsoft.socialplus.autorest.models.FeedResponseCommentView;
import com.microsoft.socialplus.server.model.FeedUserResponse;
import com.microsoft.socialplus.server.model.ListResponse;
import com.microsoft.socialplus.server.model.view.CommentView;

import java.util.ArrayList;
import java.util.List;

public class GetCommentFeedResponse extends FeedUserResponse implements ListResponse<CommentView> {

	private List<CommentView> comments;

	public GetCommentFeedResponse(List<CommentView> comments) {
		this.comments = comments;
	}

	public GetCommentFeedResponse(FeedResponseCommentView response) {
		comments = new ArrayList<>();
		for (com.microsoft.socialplus.autorest.models.CommentView comment : response.getData()) {
			comments.add(new CommentView(comment));
		}
	}

	@Override
	public List<CommentView> getData() {
		return comments;
	}
}
