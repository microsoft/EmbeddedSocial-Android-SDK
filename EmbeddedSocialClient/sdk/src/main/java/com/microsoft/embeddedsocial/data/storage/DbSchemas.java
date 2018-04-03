/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage;

/**
 * Constants defining DB schemas used by the app.
 */
public final class DbSchemas {

	private DbSchemas() {  }

	/**
	 * DB schema for added posts table.
	 */
	public static final class AddedPosts {
		private AddedPosts() {  }

		/**
		 * Table name.
		 */
		public static final String TABLE_NAME = "added_posts";
	}

	/**
	 * DB schema for topics table.
	 */
	public static final class Topics {

		private Topics() {  }

		public static final String TABLE_NAME = "topics";
		public static final String USER = "user";
		public static final String CREATED_TIME = "createdTime";
		public static final String TOPIC_HANDLE = "topicHandle";
		public static final String LIKE_STATUS = "likeStatus";
		public static final String PIN_STATUS = "pinStatus";
		public static final String TOTAL_LIKES = "totalLikes";
		public static final String TOTAL_COMMENTS = "totalComments";
		public static final String TOPIC_TITLE = "topicTitle";
		public static final String TOPIC_TEXT = "topicText";
	}

	public static final class CompactUserData {

		private CompactUserData() {  }

		public static final String TABLE_NAME = "users_compact";
		public static final String FOLLOWER_STATUS = "followerStatus";
	}

	public static final class Apps {
		private Apps() {  }

		public static final String TABLE_NAME = "apps";
	}

	public static class TopicFeedRelation {
		public static final String TABLE_NAME = "topic_feeds";
		public static final String TOPIC_HANDLE = "topicHandle";
		public static final String FEED_TYPE = "feedType";
		public static final String QUERY = "query";
	}

	public static class PinStatus {
		public static final String TABLE_NAME = "pin_status";
		public static final String TOPIC_HANDLE = "topicHandle";
		public static final String PIN_STATUS = "status";
	}

	public static class LikeStatus {
		public static final String TABLE_NAME = "like_status";
		public static final String CONTENT_HANDLE = "contentHandle";
		public static final String CONTENT_TYPE = "contentType";
		public static final String STATUS = "status";
	}

	/**
	 * DB schema for comments table.
	 */
	public static class Comments {
		public static final String TABLE_NAME = "comments";
		public static final String COMMENT_HANDLE = "commentHandle";
		public static final String TOPIC_HANDLE = "topicHandle";
		public static final String CREATED_TIME = "createdTime";
		public static final String TOTAL_LIKES = "totalLikes";
		public static final String TOTAL_REPLIES = "totalReplies";
		public static final String LIKE_STATUS = "likeStatus";
	}

	public static class CommentFeedRelation {
		public static final String TABLE_NAME = "comment_feeds";
		public static final String TOPIC_HANDLE = "topicHandle";
		public static final String COMMENT_HANDLE = "commentHandle";
		public static final String FEED_TYPE = "feedType";
	}

	public static class Replies {
		public static final String TABLE_NAME = "replies";
		public static final String REPLY_HANDLE = "replyHandle";
		public static final String COMMENT_HANDLE = "commentHandle";
		public static final String TOTAL_LIKES = "totalLikes";
		public static final String LIKE_STATUS = "likeStatus";
	}

	public static class RemoveActions {
		public static final String CONTENT_TYPE = "contentType";
		public static final String CONTENT_HANDLE = "contentHandle";
	}

	public static class UserFeeds {
		public static final String TABLE_NAME = "user_feed";
		public static final String USER_HANDLE = "userHandle";
		public static final String FEED_TYPE = "feedType";
		public static final String QUERIED_USER_HANDLE = "queriedUserHandle";
	}

	public static class SearchHistory {
		public static final String TABLE_NAME = "search_history";
		public static final String ID = "id";
		public static final String SEARCH_TYPE = "search_type";
		public static final String QUERY_TEXT = "text";
	}

	public static class UserActivity {
		public static final String TABLE_NAME = "activity";
		public static final String ACTIVITY_HANDLE = "activityHandle";
	}

	public static class ActivityActor {
		public static final String TABLE_NAME = "activity_actors";
	}

	public static class ActivityFeed {
		public static final String TABLE_NAME = "activity_feeds";
		public static final String FEED_TYPE = "feedType";
	}

	public static class UserRelationOperation {
		public static final String TABLE_NAME = "user_relation_operation";
		public static final String ACTION = "action";
		public static final String USER_HANDLE = "userHandle";
		public static final String OWNER_HANDLE = "ownerHandle";
	}

	public static class UserProfile {
		public static final String TABLE_NAME = "user_profile";
		public static final String USER_HANDLE = "userHandle";
		public static final String FOLLOWER_STATUS = "followerStatus";
		public static final String FOLLOWING_STATUS = "followingStatus";
	}

	public static class UserAccount {
		public static final String TABLE_NAME = "user_account";
	}

	public static class ThirdPartyAccount {
		public static final String TABLE_NAME = "thirdparty_account";
		public static final String ACCOUNT_HANDLE = "accountHandle";
	}

	public static class UserAccountBinding {
		public static final String TABLE_NAME = "user_account_binding";
	}

	public static class ReportContentOperation {
		public static final String TABLE_NAME = "content_report";
		public static final String CONTENT_HANDLE = "contentHandle";
		public static final String CONTENT_TYPE = "contentType";
	}

	public static class HideTopicAction {
		public static final String TABLE_NAME = "hidden_topic";
	}

	public class DiscussionItem {
		public static final String TABLE_NAME = "discussion_item";
		public static final String ID = "id";
		public static final String ROOT_HANDLE = "rootHandle";
		public static final String CONTENT_TEXT = "contentText";
		public static final String CONTENT_TYPE = "contentType";
		public static final String IMAGE_PATH = "imagePath";
	}

	public class EditedTopic {
		public static final String TABLE_NAME = "edited_topic";
	}
}
