/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.microsoft.embeddedsocial.autorest.models.PublisherType;
import com.microsoft.embeddedsocial.data.model.AccountData;
import com.microsoft.embeddedsocial.data.model.DiscussionItem;
import com.microsoft.embeddedsocial.data.storage.UserActionProxy;
import com.microsoft.embeddedsocial.event.content.CommentAddedEvent;
import com.microsoft.embeddedsocial.event.content.CommentPostedToBackendEvent;
import com.microsoft.embeddedsocial.event.content.CommentRemovedEvent;
import com.microsoft.embeddedsocial.event.content.PinRemovedEvent;
import com.microsoft.embeddedsocial.event.content.TopicRemovedEvent;
import com.microsoft.embeddedsocial.event.relationship.UserFollowedStateChangedEvent;
import com.microsoft.embeddedsocial.fetcher.CommentFeedFetcherFromTopicName;
import com.microsoft.embeddedsocial.fetcher.EmptyDataException;
import com.microsoft.embeddedsocial.fetcher.FetchersFactory;
import com.microsoft.embeddedsocial.fetcher.base.Callback;
import com.microsoft.embeddedsocial.fetcher.base.Fetcher;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.server.exception.StatusException;
import com.microsoft.embeddedsocial.server.model.view.TopicView;
import com.microsoft.embeddedsocial.server.model.view.UserCompactView;
import com.microsoft.embeddedsocial.ui.adapter.DiscussionFeedAdapter;
import com.microsoft.embeddedsocial.event.content.PinAddedEvent;
import com.microsoft.embeddedsocial.service.IntentExtras;
import com.squareup.otto.Subscribe;

import java.util.HashMap;

public class CommentFeedFragment extends DiscussionFeedFragment {

	private String topicHandle;
	private Fetcher<Object> commentFeedFetcher;
	private HashMap<Integer, Integer> errorMessages;

	private final Callback callback = new Callback() {

		@Override
		public void onDataRequestFailed(Exception e) {
			if (e instanceof EmptyDataException) {
				Intent returnIntent = new Intent();
				returnIntent.putExtra(IntentExtras.TOPIC_REMOVE_EXTRA, getHandle());
				getActivity().setResult(Activity.RESULT_OK, returnIntent);
				getActivity().finish();
				Toast.makeText(getActivity(), R.string.es_message_failed_to_refresh_topic, Toast.LENGTH_LONG).show();
			}
		}
	};

	public CommentFeedFragment() {
		addThemeToMerge(R.style.EmbeddedSocialSdkAppTheme_LightBase);
	}

	public static CommentFeedFragment getCommentFeedFragmentFromTopicHandle(String topicHandle, HashMap<Integer, Integer> errorMessages) {
		CommentFeedFragment feedFragment = new CommentFeedFragment();
		Bundle b = new Bundle();
		b.putCharSequence(IntentExtras.TOPIC_HANDLE, topicHandle);
		b.putSerializable(IntentExtras.ERROR_MESSAGES_EXTRA, errorMessages);
		feedFragment.setArguments(b);
		return feedFragment;
	}

	public static CommentFeedFragment getCommentFeedFragmentFromTopicName(String topicName, PublisherType publisherType, HashMap<Integer, Integer> errorMessages) {
		CommentFeedFragment feedFragment = new CommentFeedFragment();
		Bundle b = new Bundle();
		b.putCharSequence(IntentExtras.TOPIC_NAME, topicName);
		b.putSerializable(IntentExtras.PUBLISHER_TYPE, publisherType.toValue());
		b.putSerializable(IntentExtras.ERROR_MESSAGES_EXTRA, errorMessages);
		feedFragment.setArguments(b);
		return feedFragment;
	}

	@Override
	protected DiscussionFeedAdapter createInitialAdapter() {
		Bundle arguments = getArguments();

		errorMessages = (HashMap)arguments.getSerializable(IntentExtras.ERROR_MESSAGES_EXTRA);

		topicHandle = arguments.getString(IntentExtras.TOPIC_HANDLE);

		if (topicHandle == null) {
			// Get topic from name
			String topicName = arguments.getString(IntentExtras.TOPIC_NAME);
			PublisherType publisherType = PublisherType.fromValue(arguments.getString(IntentExtras.PUBLISHER_TYPE));

			commentFeedFetcher = FetchersFactory.createCommentFeedFetcherFromTopicName(
					topicName,
					publisherType
			);
		} else if (commentFeedFetcher == null) {
			commentFeedFetcher = FetchersFactory.createCommentFeedFetcher(
					topicHandle,
					arguments.getParcelable(IntentExtras.TOPIC_EXTRA)
			);
		}

		DiscussionFeedAdapter adapter = new DiscussionFeedAdapter(
			getActivity(),
			commentFeedFetcher,
			DiscussionFeedAdapter.FeedType.COMMENT);

		adapter.addFetcherCallback(callback);

		return adapter;
	}

	@Override
	protected String getErrorMessage(Exception exception) {
		if (errorMessages != null && exception instanceof StatusException) {
			int code = ((StatusException)exception).getStatusCode();
			if (errorMessages.containsKey(code)) {
				return getString(errorMessages.get(code));
			}
		}

		// default message
		return super.getErrorMessage(exception);
	}

	@Override
	protected int getNoteHint() {
		return R.string.es_hint_add_comment;
	}

	@Override
	protected AccountData getAuthorProfile() {
		return commentFeedFetcher.isEmpty() ? null : ((TopicView) commentFeedFetcher.getAllData().get(0)).getUserProfile();
	}

	@Override
	protected UserCompactView getAuthor() {
		return commentFeedFetcher.isEmpty() ? null : ((TopicView) commentFeedFetcher.getAllData().get(0)).getUser();
	}

	@Override
	protected String getHandle() {
		if (topicHandle == null && commentFeedFetcher instanceof CommentFeedFetcherFromTopicName) {
			topicHandle = ((CommentFeedFetcherFromTopicName)commentFeedFetcher).getTopicHandle();
		}
		return topicHandle;
	}

	@Override
	protected void onDonePressed(String text, String imagePath) {
		DiscussionItem discussionItem = DiscussionItem.newComment(getHandle(), text, imagePath);
		new UserActionProxy(getActivity()).postComment(discussionItem);
	}

	@Subscribe
	public void onTopicRemoved(TopicRemovedEvent topicRemovedEvent) {
		if (topicRemovedEvent.isSuccessful()) {
			Toast.makeText(getActivity(), R.string.es_content_removed_topic, Toast.LENGTH_SHORT).show();
			Intent returnIntent = new Intent();
			returnIntent.putExtra(IntentExtras.TOPIC_REMOVE_EXTRA, getHandle());
			getActivity().setResult(Activity.RESULT_OK, returnIntent);
			getActivity().finish();
		}
	}

	@Subscribe
	public void onPinAdded(PinAddedEvent pinAddedEvent) {
		if (pinAddedEvent.isResult()) {
			getAdapter().setTopicPin(true);
		}
	}

	@Subscribe
	public void onPinRemoved(PinRemovedEvent pinRemovedEvent) {
		if (pinRemovedEvent.isResult()) {
			getAdapter().setTopicPin(false);
		}
	}

	@Subscribe
	public void onCommentRemoved(CommentRemovedEvent commentRemovedEvent) {
		if (commentRemovedEvent.isSuccessful()) {
			getAdapter().removeComment(commentRemovedEvent.getData().getHandle());
			Toast.makeText(getActivity(), R.string.es_content_removed_comment, Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getActivity(), R.string.es_message_failed_to_remove_comment, Toast.LENGTH_SHORT).show();
		}
	}

	@Subscribe
	public void onCommentAdded(CommentAddedEvent commentAddedEvent) {
		if (commentAddedEvent.isResult()) {
			getAdapter().addComment(commentAddedEvent.getData());
		}
	}

	@Subscribe
	public void onCommentPostedToBackend(CommentPostedToBackendEvent event) {
		getAdapter().refreshData();
	}

	@Subscribe
	public void onFollowedStatusChanged(UserFollowedStateChangedEvent event) {
		getAdapter().setFollowerStatus(event.getUserHandle(), event.getFollowedStatus());
	}

}
