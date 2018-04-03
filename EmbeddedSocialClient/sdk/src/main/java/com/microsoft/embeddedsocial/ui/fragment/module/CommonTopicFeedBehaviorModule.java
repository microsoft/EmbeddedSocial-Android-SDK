/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.fragment.module;

import android.support.annotation.NonNull;

import com.microsoft.embeddedsocial.base.event.EventBus;
import com.microsoft.embeddedsocial.event.content.CommentAddedEvent;
import com.microsoft.embeddedsocial.event.content.LikeAddedEvent;
import com.microsoft.embeddedsocial.event.content.LikeRemovedEvent;
import com.microsoft.embeddedsocial.event.content.PinAddedEvent;
import com.microsoft.embeddedsocial.event.content.PinRemovedEvent;
import com.microsoft.embeddedsocial.event.content.TopicRemovedEvent;
import com.microsoft.embeddedsocial.event.relationship.UserFollowedStateChangedEvent;
import com.microsoft.embeddedsocial.fetcher.base.FetchableAdapter;
import com.microsoft.embeddedsocial.server.model.view.TopicView;
import com.microsoft.embeddedsocial.ui.fragment.base.BaseFeedFragment;
import com.microsoft.embeddedsocial.ui.fragment.base.Module;
import com.squareup.otto.Subscribe;

import java.util.List;

/**
 * Common behavior for fragments showing topic feeds.
 */
public class CommonTopicFeedBehaviorModule extends Module {

	private final BaseFeedFragment owner;

	public CommonTopicFeedBehaviorModule(BaseFeedFragment owner) {
		super(owner);
		this.owner = owner;
	}

	@Override
	protected void onPause() {
		EventBus.unregister(this);
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		EventBus.register(this);
	}

	@Subscribe
	public void onCommentAdded(CommentAddedEvent commentAddedEvent) {
		if (commentAddedEvent.isResult()) {
			FetchableAdapter<TopicView, ?> adapter = owner.getAdapter();
			if (adapter == null) {
				return;
			}
			final TopicView topic = findTopic(commentAddedEvent.getData().getRootHandle());
			if (topic != null) {
				topic.setTotalComments(topic.getTotalComments() + 1);
				adapter.notifyDataSetChanged();
			}
		}
	}

	@Subscribe
	public void onLikeAdded(LikeAddedEvent likeCompletedEvent) {
		if (likeCompletedEvent.isResult()) {
			setLike(likeCompletedEvent.getData().getHandle(), true);
		}
	}

	@Subscribe
	public void onLikeRemoved(LikeRemovedEvent likeRemovedEvent) {
		if (likeRemovedEvent.isResult()) {
			setLike(likeRemovedEvent.getData().getHandle(), false);
		}
	}

	@Subscribe
	public void onPinAdded(PinAddedEvent pinAddedEvent) {
		if (pinAddedEvent.isResult()) {
			setPin(pinAddedEvent.getData().getHandle(), true);
		}
	}

	@Subscribe
	public void onPinRemoved(PinRemovedEvent pinRemovedEvent) {
		if (pinRemovedEvent.isResult()) {
			setPin(pinRemovedEvent.getData().getHandle(), false);
		}
	}

	@Subscribe
	public void onTopicRemoved(TopicRemovedEvent event) {
		if (event.isSuccessful()) {
			FetchableAdapter<TopicView, ?> adapter = owner.getAdapter();
			if (adapter == null) {
				return;
			}
			String topicHandle = event.getData().getHandle();
			adapter.removeFirstMatch(
				item -> TopicView.class.isInstance(item) && topicHandle.equals(item.getHandle())
			);
		}
	}

	@Subscribe
	public void onUserFollowedStatusChanged(UserFollowedStateChangedEvent event) {
		FetchableAdapter<TopicView, ?> adapter = owner.getAdapter();
		if (adapter == null) {
			return;
		}
		List<TopicView> topics = adapter.getFetcher().getAllData();
		String eventUserHandle = event.getUserHandle();
		for (TopicView topic : topics) {
			if (eventUserHandle.equals(topic.getUser().getHandle())) {
				topic.getUser().setFollowerStatus(event.getFollowedStatus());
			}
		}
	}

	private void setLike(@NonNull String topicHandle, boolean likeStatus) {
		FetchableAdapter<TopicView, ?> adapter = owner.getAdapter();
		if (adapter == null) {
			return;
		}
		final TopicView topic = findTopic(topicHandle);
		if (setLike(topic, likeStatus)) {
			adapter.notifyDataSetChanged();
		}
	}

	private void setPin(@NonNull String topicHandle, boolean pinStatus) {
		FetchableAdapter<TopicView, ?> adapter = owner.getAdapter();
		if (adapter == null) {
			return;
		}
		final TopicView topic = findTopic(topicHandle);
		if (topic != null && topic.isPinStatus() != pinStatus) {
			topic.setPinStatus(pinStatus);
			adapter.notifyDataSetChanged();
		}
	}

	private TopicView findTopic(@NonNull String topicHandle) {
		FetchableAdapter<TopicView, ?> adapter = owner.getAdapter();
		if (adapter != null) {
			for (TopicView topic : adapter.getFetcher().getAllData()) {
				if (topicHandle.equals(topic.getHandle())) {
					return topic;
				}
			}
		}
		return null;
	}

	private boolean setLike(TopicView topic, boolean likeStatus) {
		if (topic != null && topic.isLikeStatus() != likeStatus) {
			topic.setLikeStatus(likeStatus);
			topic.setTotalLikes(
				(likeStatus)
					? topic.getTotalLikes() + 1
					: Math.max(0, topic.getTotalLikes() - 1)
			);
			return true;
		}
		return false;
	}

}
