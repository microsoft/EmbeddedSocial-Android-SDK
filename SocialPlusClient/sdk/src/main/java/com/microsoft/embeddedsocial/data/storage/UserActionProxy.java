/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage;

import android.content.Context;

import com.microsoft.embeddedsocial.account.UserAccount;
import com.microsoft.embeddedsocial.data.model.AccountData;
import com.microsoft.embeddedsocial.data.model.DiscussionItem;
import com.microsoft.embeddedsocial.data.model.LikeContentData;
import com.microsoft.embeddedsocial.data.model.PinData;
import com.microsoft.embeddedsocial.data.model.RemoveContentData;
import com.microsoft.embeddedsocial.event.content.CommentAddedEvent;
import com.microsoft.embeddedsocial.event.content.CommentRemovedEvent;
import com.microsoft.embeddedsocial.event.content.ReplyAddedEvent;
import com.microsoft.embeddedsocial.sdk.BuildConfig;
import com.microsoft.embeddedsocial.server.model.view.CommentView;
import com.microsoft.embeddedsocial.server.model.view.TopicView;
import com.microsoft.embeddedsocial.service.WorkerService;
import com.microsoft.embeddedsocial.autorest.models.ContentType;
import com.microsoft.embeddedsocial.autorest.models.Reason;
import com.microsoft.embeddedsocial.base.event.AbstractEvent;
import com.microsoft.embeddedsocial.base.event.EventBus;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.data.storage.model.EditedTopic;
import com.microsoft.embeddedsocial.event.content.LikeAddedEvent;
import com.microsoft.embeddedsocial.event.content.LikeRemovedEvent;
import com.microsoft.embeddedsocial.event.content.PinAddedEvent;
import com.microsoft.embeddedsocial.event.content.PinRemovedEvent;
import com.microsoft.embeddedsocial.event.content.ReplyRemovedEvent;
import com.microsoft.embeddedsocial.event.content.TopicRemovedEvent;
import com.microsoft.embeddedsocial.server.model.view.ReplyView;
import com.microsoft.embeddedsocial.service.ServiceAction;

import java.sql.SQLException;

/**
 * Is used as a facade class allowing to perform user actions such as likes/pins/etc.
 */
public class UserActionProxy {

	private final UserActionCache actionCache = new UserActionCache();
	private final ContentCache contentCache = new ContentCache();
	private final UserCache userCache = new UserCache();
	private final PostStorage postStorage;
	private final Context context;

	/**
	 * Creates an instance.
	 *
	 * @param context valid context
	 */
	public UserActionProxy(Context context) {
		this.context = context.getApplicationContext();
		postStorage = new PostStorage(context);
	}

	/**
	 * Sets topic pin status
	 *
	 * @param topicHandle topic handle
	 * @param pinned      topic pin status
	 */
	public void setPinStatus(String topicHandle, boolean pinned) {
		checkAuthorization();
		actionCache.setPinStatus(topicHandle, pinned);

		PinData data = new PinData(topicHandle);
		AbstractEvent pinEvent = pinned ? new PinAddedEvent(data, true)
			: new PinRemovedEvent(data, true);

		pinEvent.submit();
		launchSync();
	}

	private void launchSync() {
		WorkerService.getLauncher(context).launchService(ServiceAction.SYNC_DATA);
	}

	/**
	 * Sets content like status.
	 *
	 * @param contentHandle content handle
	 * @param contentType   content type
	 * @param liked         like status
	 */
	public void setLikeStatus(String contentHandle, ContentType contentType, boolean liked) {
		checkAuthorization();
		actionCache.setLikeStatus(contentHandle, contentType, liked);
		LikeContentData data = new LikeContentData(contentHandle, contentType);
		AbstractEvent likeEvent = liked ? new LikeAddedEvent(data, true)
			: new LikeRemovedEvent(data, true);
		likeEvent.submit();
		launchSync();
	}

	/**
	 * Removes a topic.
	 *
	 * @param topic topic to remove
	 */
	public void removeTopic(TopicView topic) {
		checkAuthorization();
		if (!topic.isLocal()) {
			contentCache.removeTopic(topic.getHandle());
			addContentRemovalRequest(topic.getHandle(), ContentType.TOPIC);
			launchSync();
		} else {
			postStorage.removePostById(topic.getLocalPostId());
		}
		new TopicRemovedEvent(new RemoveContentData(topic.getHandle()), true).submit();
	}

	/**
	 * Removes a comment.
	 */
	public void removeComment(CommentView comment) {
		checkAuthorization();
		if (comment.isLocal()) {
			postStorage.removeUnsentComment(comment.getOfflineId());
		} else {
			addContentRemovalRequest(comment.getHandle(), ContentType.COMMENT);
			contentCache.removeComment(comment.getHandle());
		}
		new CommentRemovedEvent(new RemoveContentData(comment.getHandle()), true).submit();
		launchSync();
	}

	/**
	 * Removes a reply.
	 */
	public void removeReply(ReplyView reply) {
		checkAuthorization();
		if (!reply.isLocal()) {
			try {
				addContentRemovalRequest(reply.getHandle(), ContentType.REPLY);
				contentCache.removeReply(reply.getReplyHandle());
			} catch (SQLException e) {
				DebugLog.logException(e);
			}
		} else {
			postStorage.removeUnsentReply(reply.getOfflineId());
		}
		new ReplyRemovedEvent(new RemoveContentData(reply.getHandle()), true).submit();
		launchSync();
	}

	/**
	 * Reports a piece of content as inappropriate.
	 *
	 * @param contentHandle content handle
	 * @param contentType   type of content
	 * @param reason        report reason
	 */
	public void reportContent(String contentHandle, ContentType contentType, Reason reason) {
		checkAuthorization();
		actionCache.reportContent(contentHandle, contentType, reason);
		launchSync();
	}

	/**
	 * Reports a user as inappropriate.
	 *
	 * @param userHandle user handle
	 * @param reason     report reason
	 */
	public void reportUser(String userHandle, Reason reason) {
		checkAuthorization();
		actionCache.reportUser(userHandle, reason);
		launchSync();
	}

	/**
	 * Accepts pending follow request.
	 * @param userHandle    handle of the user to accept
	 */
	public void acceptUser(String userHandle) {
		checkAuthorization();
		userCache.acceptUser(userHandle);
		AccountData account = UserAccount.getInstance().getAccountDetails();
		account.setFollowersCount(account.getFollowersCount() + 1);
		launchSync();
	}

	/**
	 * Blocks a user.
	 * @param userHandle    handle of the user to block
	 */
	public void blockUser(String userHandle) {
		checkAuthorization();
		userCache.blockUser(userHandle);
		AccountData account = UserAccount.getInstance().getAccountDetails();
		account.setFollowersCount(Math.max(account.getFollowingCount() - 1, 0));
		launchSync();
	}

	/**
	 * Follows a user.
	 * @param userHandle    handle of the user to follow
	 */
	public void followUser(String userHandle) {
		checkAuthorization();
		userCache.followUser(userHandle);
		launchSync();
	}

	/**
	 * Rejects pending follow request.
	 * @param userHandle    user handle
	 */
	public void rejectUser(String userHandle) {
		checkAuthorization();
		userCache.rejectUser(userHandle);
		launchSync();
	}

	/**
	 * Unblocks a user.
	 * @param userHandle    user handle
	 */
	public void unblockUser(String userHandle) {
		checkAuthorization();
		userCache.unblockUser(userHandle);
		launchSync();
	}

	/**
	 * Unfollows a user.
	 * @param userHandle    user handle
	 */
	public void unfollowUser(String userHandle) {
		checkAuthorization();
		userCache.unfollowUser(userHandle);
		AccountData account = UserAccount.getInstance().getAccountDetails();
		account.setFollowingCount(Math.max(account.getFollowingCount() - 1, 0));
		launchSync();
	}

	/**
	 * Hides a topic.
	 * @param topicHandle   topic handle
	 */
	public void hideTopic(String topicHandle) {
		checkAuthorization();
		actionCache.hideTopic(topicHandle);
		EventBus.post(new TopicRemovedEvent(new RemoveContentData(topicHandle), true));
		launchSync();
	}

	private void addContentRemovalRequest(String contentHandle, ContentType contentType) {
		actionCache.addContentRemovalAction(contentHandle, contentType);
	}

	/**
	 * Posts a comment.
	 * @param discussionItem    the comment to post
	 */
	public void postComment(DiscussionItem discussionItem) {
		checkAuthorization();
		try {
			postStorage.storeDiscussionItem(discussionItem);
			launchSync();
			EventBus.post(new CommentAddedEvent(discussionItem, null, true));
		} catch (SQLException e) {
			DebugLog.logException(e);
		}
	}

	/**
	 * Updates a topic.
	 * @param topic updated topic
	 */
	public void updateTopic(TopicView topic) {
		EditedTopic editedTopic = new EditedTopic(topic.getHandle(), topic.getTopicTitle(),
				topic.getTopicText(), topic.getTopicCategory());
		try {
			postStorage.storeEditedTopic(editedTopic);
			launchSync();
		} catch (SQLException e) {
			DebugLog.logException(e);
		}
	}

	/**
	 * Posts a reply.
	 * @param discussionItem    reply to post
	 */
	public void postReply(DiscussionItem discussionItem) {
		checkAuthorization();
		try {
			postStorage.storeDiscussionItem(discussionItem);
			launchSync();
			EventBus.post(new ReplyAddedEvent(discussionItem, null, true));
		} catch (SQLException e) {
			DebugLog.logException(e);
		}
	}

	private void checkAuthorization() {
		if (!UserAccount.getInstance().isSignedIn() && BuildConfig.DEBUG) {
			throw new RuntimeException("not authorized");
		}
	}

}
