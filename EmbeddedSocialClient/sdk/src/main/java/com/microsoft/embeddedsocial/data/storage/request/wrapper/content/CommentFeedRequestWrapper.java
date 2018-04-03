/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage.request.wrapper.content;

import android.content.Context;

import com.microsoft.embeddedsocial.data.storage.ContentCache;
import com.microsoft.embeddedsocial.data.storage.PostStorage;
import com.microsoft.embeddedsocial.data.storage.request.wrapper.AbstractBatchNetworkMethodWrapper;
import com.microsoft.embeddedsocial.server.model.content.comments.GetCommentFeedRequest;
import com.microsoft.embeddedsocial.server.model.content.comments.GetCommentFeedResponse;
import com.microsoft.embeddedsocial.server.model.view.CommentView;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class CommentFeedRequestWrapper extends AbstractBatchNetworkMethodWrapper<GetCommentFeedRequest, GetCommentFeedResponse> {

	private final PostStorage postStorage;
	private ContentCache contentCache;

	public CommentFeedRequestWrapper(INetworkMethod<GetCommentFeedRequest, GetCommentFeedResponse> networkMethod,
									 ContentCache contentCache, Context context) {

		super(networkMethod);
		this.contentCache = contentCache;
		this.postStorage = new PostStorage(context);
	}

	@Override
	protected void storeResponse(GetCommentFeedRequest request, GetCommentFeedResponse response)
		throws SQLException {

		contentCache.storeCommentFeed(request, response);
	}

	@Override
	protected GetCommentFeedResponse getCachedResponse(GetCommentFeedRequest request) throws SQLException {
		return contentCache.getCommentFeedResponse(request);
	}

	@Override
	protected void onResponseIsReady(GetCommentFeedRequest request, GetCommentFeedResponse response,
	                                 boolean cachedResponseUsed) {

		response.getData().addAll(0, getLocalComments(request));
	}

	private List<CommentView> getLocalComments(GetCommentFeedRequest request) {
		List<CommentView> localComments;
		try {
			localComments = postStorage.getUnsentComments(request.getTopicHandle());
		} catch (SQLException e) {
			localComments = Collections.emptyList();
		}

		return localComments;
	}
}
