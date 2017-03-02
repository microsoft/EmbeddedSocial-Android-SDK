/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.microsoft.embeddedsocial.base.event.EventBus;
import com.microsoft.embeddedsocial.data.model.AccountData;
import com.microsoft.embeddedsocial.data.model.DiscussionItem;
import com.microsoft.embeddedsocial.data.storage.UserActionProxy;
import com.microsoft.embeddedsocial.event.content.CommentRemovedEvent;
import com.microsoft.embeddedsocial.event.content.ReplyAddedEvent;
import com.microsoft.embeddedsocial.event.content.ReplyPostedToBackendEvent;
import com.microsoft.embeddedsocial.event.content.ReplyRemovedEvent;
import com.microsoft.embeddedsocial.fetcher.FetchersFactory;
import com.microsoft.embeddedsocial.fetcher.base.Fetcher;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.server.model.view.CommentView;
import com.microsoft.embeddedsocial.server.model.view.UserCompactView;
import com.microsoft.embeddedsocial.ui.adapter.DiscussionFeedAdapter;
import com.microsoft.embeddedsocial.service.IntentExtras;
import com.squareup.otto.Subscribe;

public class ReplyFeedFragment extends DiscussionFeedFragment {

	private String commentHandle;
	private Fetcher<Object> commentFeedFetcher;

	@Override
	protected DiscussionFeedAdapter createInitialAdapter() {
		Bundle arguments = getArguments();
		commentHandle = arguments.getString(IntentExtras.COMMENT_HANDLE);
		if (commentFeedFetcher == null) {
			commentFeedFetcher = FetchersFactory.createReplyFeedFetcher(
				commentHandle,
				arguments.getParcelable(IntentExtras.COMMENT_EXTRA)
			);
		}
		return new DiscussionFeedAdapter(getActivity(), commentFeedFetcher, DiscussionFeedAdapter.FeedType.REPLY);
	}

	@Override
	protected void initRecyclerView() {
		super.initRecyclerView();
		if (imageButton != null) {
			// could be null if user is not signed in or the comment is local
			imageButton.setVisibility(View.GONE);
		}
	}

	@Override
	protected int getNoteHint() {
		return R.string.es_hint_add_reply;
	}

	@Override
	protected void onDonePressed(String text, String imagePath) {
		DiscussionItem noteData = DiscussionItem.newReply(getHandle(), text);
		new UserActionProxy(getActivity()).postReply(noteData);
	}

	@Override
	protected String getHandle() {
		return commentHandle;
	}

	@Override
	public void onResume() {
		super.onResume();
		EventBus.register(eventListener);
	}

	@Override
	public void onPause() {
		super.onPause();
		EventBus.unregister(eventListener);
	}

	@Override
	protected AccountData getAuthorProfile() {
		return commentFeedFetcher.isEmpty() ? null : ((CommentView) commentFeedFetcher.getAllData().get(0)).getUserProfile();
	}

	@Override
	protected UserCompactView getAuthor() {
		return commentFeedFetcher.isEmpty() ? null : ((CommentView) commentFeedFetcher.getAllData().get(0)).getUser();
	}

	private Object eventListener = new Object() {

		@Subscribe
		public void onReplyRemoved(ReplyRemovedEvent replyRemovedEvent) {
			if (replyRemovedEvent.isSuccessful()) {
				getAdapter().removeReply(replyRemovedEvent.getData().getHandle());
				Toast.makeText(getActivity(), R.string.es_content_removed_reply, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getActivity(), R.string.es_message_failed_to_remove_reply, Toast.LENGTH_SHORT).show();
			}
		}

		@Subscribe
		public void onReplyAdded(ReplyAddedEvent replyAddedEvent) {
			if (replyAddedEvent.isResult()) {
				getAdapter().addReply(replyAddedEvent.getData());
			}
		}

		@Subscribe
		public void onCommentRemoved(CommentRemovedEvent commentRemovedEvent) {
			if (commentRemovedEvent.isSuccessful()) {
				hideKeyboard();
				Toast.makeText(getActivity(), R.string.es_content_removed_comment, Toast.LENGTH_SHORT).show();
				Intent returnIntent = new Intent();
				returnIntent.putExtra(IntentExtras.COMMENT_HANDLE, getHandle());
				getActivity().setResult(Activity.RESULT_OK, returnIntent);
				getActivity().finish();
			}
		}

		@Subscribe
		public void onReplyPostedToBackend(ReplyPostedToBackendEvent event) {
			getAdapter().refreshData();
		}
	};
}
