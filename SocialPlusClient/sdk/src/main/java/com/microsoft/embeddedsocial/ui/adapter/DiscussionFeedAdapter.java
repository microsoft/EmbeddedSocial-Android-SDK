/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.microsoft.embeddedsocial.autorest.models.PublisherType;
import com.microsoft.embeddedsocial.data.model.DiscussionItem;
import com.microsoft.embeddedsocial.fetcher.base.FetchableAdapter;
import com.microsoft.embeddedsocial.fetcher.base.Fetcher;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.server.model.view.CommentView;
import com.microsoft.embeddedsocial.server.model.view.TopicView;
import com.microsoft.embeddedsocial.ui.adapter.viewholder.FlatTopicButtonsListener;
import com.microsoft.embeddedsocial.ui.adapter.viewholder.ReplyButtonListener;
import com.microsoft.embeddedsocial.ui.adapter.viewholder.ReplyViewHolder;
import com.microsoft.embeddedsocial.ui.adapter.viewholder.TopicFlatViewHolder;
import com.microsoft.embeddedsocial.ui.adapter.viewholder.UserHeaderViewHolder;
import com.microsoft.embeddedsocial.autorest.models.FollowerStatus;
import com.microsoft.embeddedsocial.server.model.UniqueItem;
import com.microsoft.embeddedsocial.server.model.view.ReplyView;
import com.microsoft.embeddedsocial.ui.adapter.viewholder.CommentButtonListener;
import com.microsoft.embeddedsocial.ui.adapter.viewholder.CommentViewHolder;

import java.util.List;

/**
 * Adapter for single topic fragment.
 */
public class DiscussionFeedAdapter extends FetchableAdapter<Object, RecyclerView.ViewHolder> {

	private static final int VIEW_TYPE_TOPIC = 0;
	private static final int VIEW_TYPE_COMMENT = 1;
	private static final int VIEW_TYPE_REPLY = 2;

	private static final int POSITION_FIRST_ITEM = 0;
	private static final int LOAD_MORE_VIEW_POSITION = 1;

	private final FeedType feedType;
	private final FlatTopicButtonsListener topicButtonsListener;
	private final CommentButtonListener commentButtonListener;
	private final ReplyButtonListener replyButtonListener;
	private Fetcher<Object> fetcher;

	public DiscussionFeedAdapter(Context context, Fetcher<Object> fetcher, FeedType feedType) {
		super(fetcher, true);
		this.fetcher = fetcher;
		this.feedType = feedType;
		this.topicButtonsListener = new FlatTopicButtonsListener(context);
		this.commentButtonListener = new CommentButtonListener(context,
			feedType == FeedType.COMMENT
				? CommentButtonListener.Container.TOPIC
				: CommentButtonListener.Container.COMMENT);
		this.replyButtonListener = new ReplyButtonListener(context);
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		switch (viewType) {
			case VIEW_TYPE_TOPIC:
				return TopicFlatViewHolder.create(topicButtonsListener, parent);
			case VIEW_TYPE_COMMENT:
				return CommentViewHolder.create(
					commentButtonListener,
					parent,
					feedType == FeedType.COMMENT
						? UserHeaderViewHolder.HolderType.FEED
						: UserHeaderViewHolder.HolderType.CONTENT);
			case VIEW_TYPE_REPLY:
				return ReplyViewHolder.create(
					replyButtonListener,
					parent,
					feedType == FeedType.REPLY
						? UserHeaderViewHolder.HolderType.FEED
						: UserHeaderViewHolder.HolderType.CONTENT);
		}

		return null;
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		switch (getItemViewType(position)) {
			case VIEW_TYPE_TOPIC:
				((TopicFlatViewHolder) holder).renderItem(position, (TopicView) getItem(position));
				break;
			case VIEW_TYPE_COMMENT:
				((CommentViewHolder) holder).renderItem(position, (CommentView) getItem(position));
				break;
			case VIEW_TYPE_REPLY:
				((ReplyViewHolder) holder).renderItem((ReplyView) getItem(position));
		}
	}

	@Override
	public int getItemViewType(int position) {
		switch (feedType) {
			case COMMENT:
				if (position == POSITION_FIRST_ITEM) {
					return VIEW_TYPE_TOPIC;
				} else {
					return VIEW_TYPE_COMMENT;
				}
			case REPLY:
				if (position == POSITION_FIRST_ITEM) {
					return VIEW_TYPE_COMMENT;
				} else {
					return VIEW_TYPE_REPLY;
				}
		}
		return -1;
	}

	@Override
	public int getItemCount() {
		return getDataSize();
	}

	public void setTopicLike(boolean likeStatus) {
		if (!isUpdateData()) {
			return;
		}

		TopicView topicView = (TopicView) getFetcher().getAllData().get(0);

		if (topicView == null) {
			return;
		}

		if (topicView.isLikeStatus() != likeStatus) {
			topicView.setLikeStatus(likeStatus);
			topicView.setTotalLikes(
				(likeStatus) ?
					topicView.getTotalLikes() + 1 :
					Math.max(0, topicView.getTotalLikes() - 1));
			notifyDataSetChanged();
		}
	}

	public void setCommentLike(@NonNull String commentHandle, boolean likeStatus) {
		if (!isUpdateData()) {
			return;
		}

		CommentView commentView = findItem(commentHandle, CommentView.class);
		if (commentView == null) {
			return;
		}

		if (commentView.isLikeStatus() != likeStatus) {
			commentView.setLikeStatus(likeStatus);
			commentView.setTotalLikes(
				(likeStatus) ?
					commentView.getTotalLikes() + 1 :
					Math.max(0, commentView.getTotalLikes() - 1));
			notifyDataSetChanged();
		}
	}

	public void setReplyLike(@NonNull String replyHandle, boolean likeStatus) {
		if (!isUpdateData()) {
			return;
		}

		ReplyView replyView = findItem(replyHandle, ReplyView.class);
		if (replyView == null) {
			return;
		}

		if (replyView.isLikeStatus() != likeStatus) {
			replyView.setLikeStatus(likeStatus);
			replyView.setTotalLikes(
				(likeStatus) ?
					replyView.getTotalLikes() + 1 :
					Math.max(0, replyView.getTotalLikes() - 1));
			notifyDataSetChanged();
		}
	}

	public void setTopicPin(boolean pinStatus) {
		if (!isUpdateData()) {
			return;
		}

		TopicView topicView = (TopicView) getFetcher().getAllData().get(0);

		if (topicView == null) {
			return;
		}

		if (topicView.isPinStatus() != pinStatus) {
			topicView.setPinStatus(pinStatus);
			notifyDataSetChanged();
		}
	}

	public void removeComment(String commentHandle) {
		removeFirstMatch(object -> CommentView.class.isInstance(object) && commentHandle.equals(((CommentView) object).getHandle()));
	}

	public void removeReply(String replyHandle) {
		removeFirstMatch(object -> ReplyView.class.isInstance(object) && replyHandle.equals(((ReplyView) object).getHandle()));
	}

	public void setFollowerStatus(String userHandle, FollowerStatus followerStatus) {
		if (!isUpdateData()) {
			return;
		}

		TopicView topicView = (TopicView) getFetcher().getAllData().get(0);
		if (topicView.getPublisherType() == PublisherType.USER
				&& userHandle.equals(topicView.getUser().getHandle())) {
			topicView.getUser().setFollowerStatus(followerStatus);
		}

		for (int i = 0; i < getDataSize(); i++) {
			final Object commentObject = getItem(i);
			if (CommentView.class.isInstance(commentObject)) {
				CommentView comment = (CommentView) commentObject;
				if (userHandle.equals(comment.getUser().getHandle())) {
					comment.getUser().setFollowerStatus(followerStatus);
				}
			}
		}

		notifyDataSetChanged();
	}

	public void addComment(DiscussionItem discussionItem) {
		if (!isUpdateData()) {
			return;
		}

		TopicView topicView = (TopicView) getFetcher().getAllData().get(0);
		topicView.setTotalComments(topicView.getTotalComments() + 1);

		getFetcher().insertItem(discussionItem.asComment(), 1);
	}

	public void addReply(DiscussionItem discussionItem) {
		if (!isUpdateData()) {
			return;
		}

		CommentView commentView = (CommentView) getFetcher().getAllData().get(0);
		commentView.setTotalReplies(commentView.getTotalReplies() + 1);

		getFetcher().insertItem(discussionItem.asReply(), 1);
	}

	@Override
	public Object getItem(int position) {
		List<Object> data = fetcher.getAllData();
		if (position == POSITION_FIRST_ITEM) {
			return data.get(0);
		} else {
			return data.get(data.size() - position);
		}
	}

	@Override
	protected int getViewPosition(int dataItemPosition) {
		if (dataItemPosition == POSITION_FIRST_ITEM) {
			return dataItemPosition;
		} else {
			return fetcher.getAllData().size() + 1 - dataItemPosition;
		}
	}

	@Nullable
	private <T extends UniqueItem> T findItem(@NonNull String handle, Class<T> cls) {
		for (int i = 0; i < getDataSize(); i++) {
			final Object item = getItem(i);
			if (cls.isInstance(item)) {
				T result = cls.cast(item);
				if (handle.equals(result.getHandle())) {
					return result;
				}
			}
		}
		return null;
	}

	private boolean isUpdateData() {
		return !getFetcher().isEmpty() && !getFetcher().isLoading();
	}

	@Override
	protected String getLoadMoreSuggestion(Context context) {
		switch (feedType) {
			case COMMENT:
				return context.getString(R.string.es_load_more_comments);
			case REPLY:
				return context.getString(R.string.es_load_more_replies);
		}
		return "";
	}

	@Override
	protected int getPositionForWrapperViews() {
		return LOAD_MORE_VIEW_POSITION;
	}

	public enum FeedType {
		COMMENT,
		REPLY
	}
}
