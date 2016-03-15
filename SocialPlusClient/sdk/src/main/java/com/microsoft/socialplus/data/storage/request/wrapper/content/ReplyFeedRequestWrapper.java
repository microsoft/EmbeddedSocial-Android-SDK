/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.data.storage.request.wrapper.content;

import android.content.Context;

import com.microsoft.socialplus.base.utils.debug.DebugLog;
import com.microsoft.socialplus.data.storage.PostStorage;
import com.microsoft.socialplus.data.storage.TopicCache;
import com.microsoft.socialplus.data.storage.request.wrapper.AbstractBatchNetworkMethodWrapper;
import com.microsoft.socialplus.server.model.content.replies.GetReplyFeedRequest;
import com.microsoft.socialplus.server.model.content.replies.GetReplyFeedResponse;
import com.microsoft.socialplus.server.model.view.ReplyView;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class ReplyFeedRequestWrapper extends AbstractBatchNetworkMethodWrapper<GetReplyFeedRequest, GetReplyFeedResponse> {

	private final TopicCache topicCache;
	private final PostStorage postStorage;

	public ReplyFeedRequestWrapper(INetworkMethod<GetReplyFeedRequest, GetReplyFeedResponse> networkMethod,
	                               TopicCache topicCache, Context context) {

		super(networkMethod);
		this.topicCache = topicCache;
		this.postStorage = new PostStorage(context);
	}

	@Override
	protected void storeResponse(GetReplyFeedRequest request, GetReplyFeedResponse response)
		throws SQLException {

		topicCache.storeReplyFeed(request, response);
	}

	@Override
	protected GetReplyFeedResponse getCachedResponse(GetReplyFeedRequest request) throws SQLException {
		return topicCache.getReplyFeedResponse(request);
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
