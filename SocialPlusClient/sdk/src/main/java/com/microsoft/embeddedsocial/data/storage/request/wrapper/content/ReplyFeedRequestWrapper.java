/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage.request.wrapper.content;

import android.content.Context;

import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.data.storage.ContentCache;
import com.microsoft.embeddedsocial.data.storage.PostStorage;
import com.microsoft.embeddedsocial.data.storage.request.wrapper.AbstractBatchNetworkMethodWrapper;
import com.microsoft.embeddedsocial.server.model.content.replies.GetReplyFeedRequest;
import com.microsoft.embeddedsocial.server.model.content.replies.GetReplyFeedResponse;
import com.microsoft.embeddedsocial.server.model.view.ReplyView;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class ReplyFeedRequestWrapper extends AbstractBatchNetworkMethodWrapper<GetReplyFeedRequest, GetReplyFeedResponse> {

	private final ContentCache contentCache;
	private final PostStorage postStorage;

	public ReplyFeedRequestWrapper(INetworkMethod<GetReplyFeedRequest, GetReplyFeedResponse> networkMethod,
								   ContentCache contentCache, Context context) {

		super(networkMethod);
		this.contentCache = contentCache;
		this.postStorage = new PostStorage(context);
	}

	@Override
	protected void storeResponse(GetReplyFeedRequest request, GetReplyFeedResponse response)
		throws SQLException {

		contentCache.storeReplyFeed(request, response);
	}

	@Override
	protected GetReplyFeedResponse getCachedResponse(GetReplyFeedRequest request) throws SQLException {
		return contentCache.getReplyFeedResponse(request);
	}

	@Override
	protected void onResponseIsReady(GetReplyFeedRequest request, GetReplyFeedResponse response,
	                                 boolean cachedResponseUsed) {

		response.getData().addAll(0, getUnsentReplies(request));
	}

	private List<ReplyView> getUnsentReplies(GetReplyFeedRequest request) {
		List<ReplyView> result;
		try {
			result = postStorage.getUnsentReplies(request.getCommentHandle());
		} catch (SQLException e) {
			DebugLog.logException(e);
			result = Collections.emptyList();
		}

		return result;
	}
}
