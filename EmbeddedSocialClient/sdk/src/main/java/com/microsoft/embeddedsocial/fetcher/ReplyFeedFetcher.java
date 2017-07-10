/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.fetcher;

import com.microsoft.embeddedsocial.data.model.AccountData;
import com.microsoft.embeddedsocial.fetcher.base.PartiallyLoadedDataException;
import com.microsoft.embeddedsocial.fetcher.base.RequestType;
import com.microsoft.embeddedsocial.server.model.account.GetUserProfileRequest;
import com.microsoft.embeddedsocial.server.model.content.comments.GetCommentResponse;
import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.fetcher.base.DataState;
import com.microsoft.embeddedsocial.fetcher.base.Fetcher;
import com.microsoft.embeddedsocial.server.IAccountService;
import com.microsoft.embeddedsocial.server.IContentService;
import com.microsoft.embeddedsocial.server.EmbeddedSocialServiceProvider;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.content.comments.GetCommentRequest;
import com.microsoft.embeddedsocial.server.model.content.replies.GetReplyFeedRequest;
import com.microsoft.embeddedsocial.server.model.view.CommentView;
import com.microsoft.embeddedsocial.server.model.view.ReplyView;

import java.util.ArrayList;
import java.util.List;

class ReplyFeedFetcher extends Fetcher<Object> {
	private static final String COMMENT_LOADED = "commentLoaded";

	private final CommentView commentView;
	private final String commentHandle;
	private final IContentService contentService;
	private final DataRequestExecutor<ReplyView, ?> replyFeedRequestExecutor;

	public ReplyFeedFetcher(String commentHandle, CommentView commentView) {
		this.commentHandle = commentHandle;
		this.commentView = commentView;

		contentService = GlobalObjectRegistry.getObject(EmbeddedSocialServiceProvider.class).getContentService();
		replyFeedRequestExecutor = new BatchDataRequestExecutor<>(
				contentService::getReplyFeed,
				() -> new GetReplyFeedRequest(commentHandle)
		);
	}

	private CommentView readComment(RequestType requestType) throws NetworkRequestException {
		GetCommentRequest request = new GetCommentRequest(commentHandle);
		if (requestType == RequestType.SYNC_WITH_CACHE) {
			request.forceCacheUsage();
		}
		GetCommentResponse response = contentService.getComment(request);
		return response.getComment();
	}

	@Override
	protected List<Object> fetchDataPage(DataState dataState, RequestType requestType, int pageSize) throws Exception {
		List<Object> result = new ArrayList<>();
		boolean readComment = !dataState.getBooleanValue(COMMENT_LOADED);
		if (readComment) {
			result.add(getComment(requestType));
		}
		try {
			List<ReplyView> topics = replyFeedRequestExecutor.fetchData(dataState, requestType, pageSize);
			result.addAll(topics);
			return result;
		} catch (NetworkRequestException e) {
			throw readComment ? new PartiallyLoadedDataException(result, e) : e;
		} finally {
			dataState.setValue(COMMENT_LOADED, true);
		}
	}

	private CommentView getComment(RequestType requestType) throws NetworkRequestException {
		CommentView result = (commentView == null || requestType.isFullDataReloadRequired()) ? readComment(requestType) : commentView;
		IAccountService accountService = GlobalObjectRegistry.getObject(EmbeddedSocialServiceProvider.class).getAccountService();
		GetUserProfileRequest request = new GetUserProfileRequest(result.getUser().getHandle());
		if (requestType == RequestType.SYNC_WITH_CACHE) {
			request.forceCacheUsage();
		}
		AccountData profile = new AccountData(accountService.getUserProfile(request).getUser());
		result.setUserProfile(profile);
		return result;

	}
}
