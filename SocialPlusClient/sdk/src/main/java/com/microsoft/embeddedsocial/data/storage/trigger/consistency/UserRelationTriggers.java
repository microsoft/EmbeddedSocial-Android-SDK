/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage.trigger.consistency;

import com.microsoft.embeddedsocial.data.storage.DbSchemas;
import com.microsoft.embeddedsocial.data.storage.trigger.ISqlTrigger;
import com.microsoft.embeddedsocial.autorest.models.FollowingStatus;
import com.microsoft.embeddedsocial.base.expression.Template;
import com.microsoft.embeddedsocial.data.model.TopicFeedType;
import com.microsoft.embeddedsocial.data.storage.UserCache;
import com.microsoft.embeddedsocial.data.storage.trigger.TriggerGenerator;

/**
 * Cache consistency triggers for user relations.
 */
public class UserRelationTriggers {

	/**
	 * SQL triggers related to cleanup of opposite user relation operations.
	 */
	public static final ISqlTrigger[] OPERATION_CLEANUP_TRIGGERS = {

		generateCleanupTrigger(
			"blocked_user_cleanup",
			UserCache.UserRelationAction.BLOCK,
			UserCache.UserRelationAction.UNBLOCK
		),

		generateCleanupTrigger(
			"follow_user_cleanup",
			UserCache.UserRelationAction.FOLLOW,
			UserCache.UserRelationAction.UNFOLLOW
		),

		generateCleanupTrigger(
			"accept_user_cleanup",
			UserCache.UserRelationAction.ACCEPT,
			UserCache.UserRelationAction.REJECT
		),
	};

	/**
	 * SQL triggers related to data consistency in user relationship database.
	 */
	public static final ISqlTrigger[] CONSISTENCY_TRIGGERS = {

		generateDeleteUserFromFeedTrigger(
			"delete_from_blocked",
			UserCache.UserRelationAction.UNBLOCK,
			UserCache.UserFeedType.BLOCKED
		),

		generateDeleteUserFromFeedTrigger(
			"delete_accepted_from_pending",
			UserCache.UserRelationAction.ACCEPT,
			UserCache.UserFeedType.PENDING
		),

		generateDeleteUserFromFeedTrigger(
			"delete_rejected_from_pending",
			UserCache.UserRelationAction.REJECT,
			UserCache.UserFeedType.PENDING
		),

		generateDeleteUserFromFeedTrigger(
			"delete_from_following",
			UserCache.UserRelationAction.UNFOLLOW,
			UserCache.UserFeedType.FOLLOWING
		),

		new TriggerGenerator.TriggerBuilder("unfollowed_user_consistency", DbSchemas.UserRelationOperation.TABLE_NAME)
			.setAction(TriggerGenerator.DatabaseAction.AFTER_INSERT)
			.setWhen(
				new Template("new.${action} = '${unfollow}'")
					.var("action", DbSchemas.UserRelationOperation.ACTION)
					.var("unfollow", UserCache.UserRelationAction.UNFOLLOW.name())
					.render()
			).setStatements(
				new Template(
					"delete from ${tfeeds} where" +
						" (select ${user} from ${topics} where ${topics}.${thandle} = ${tfeeds}.${feed_thandle} limit 1) = new.${uhandle}" +
						" and ${ftype} = '${following_recent}'")
					.var("tfeeds", DbSchemas.TopicFeedRelation.TABLE_NAME)
					.var("user", DbSchemas.Topics.USER)
					.var("topics", DbSchemas.Topics.TABLE_NAME)
					.var("thandle", DbSchemas.Topics.TOPIC_HANDLE)
					.var("feed_thandle", DbSchemas.TopicFeedRelation.TOPIC_HANDLE)
					.var("uhandle", DbSchemas.UserRelationOperation.USER_HANDLE)
					.var("ftype", DbSchemas.TopicFeedRelation.FEED_TYPE)
					.var("following_recent", TopicFeedType.FOLLOWING_RECENT.name())
					.render()
		).build(),

		generateInsertUserToFeedTrigger(
			"add_blocked_to_feed",
			UserCache.UserRelationAction.BLOCK,
			UserCache.UserFeedType.BLOCKED,
			false
		),

		generateInsertUserToFeedTrigger(
			"add_follower_to_feed",
			UserCache.UserRelationAction.ACCEPT,
			UserCache.UserFeedType.FOLLOWER,
			true
		),

		generateUpdateUserStatusTrigger(
			"follow_user_status_update",
			UserCache.UserRelationAction.FOLLOW,
			FollowingStatus.PENDING
		),

		generateUpdateUserStatusTrigger(
			"block_user_status_update",
			UserCache.UserRelationAction.BLOCK,
				FollowingStatus.BLOCKED
		),

		generateUpdateUserStatusTrigger(
			"unblock_user_status_update",
			UserCache.UserRelationAction.UNBLOCK,
				FollowingStatus.NONE
		),

		generateUpdateUserStatusTrigger(
			"unfollow_user_status_update",
			UserCache.UserRelationAction.UNFOLLOW,
				FollowingStatus.NONE
		),

		new TriggerGenerator.TriggerBuilder("update_blocked_user_status", DbSchemas.UserRelationOperation.TABLE_NAME)
			.setAction(TriggerGenerator.DatabaseAction.AFTER_INSERT)
			.setStatements(
				new Template("update ${user_profile} set ${following_status} = ${blocked}"
					+ " where ${user_handle} = new.${user_handle}")
					.var("user_profile", DbSchemas.UserProfile.TABLE_NAME)
					.var("following_status", DbSchemas.UserProfile.FOLLOWING_STATUS)
					.var("blocked", FollowingStatus.BLOCKED.toValue())
					.var("user_handle", DbSchemas.UserFeeds.USER_HANDLE)
					.render()
			).build()
	};

	private static ISqlTrigger generateDeleteUserFromFeedTrigger(String triggerName,
		UserCache.UserRelationAction action, UserCache.UserFeedType feedType) {

		return new TriggerGenerator.TriggerBuilder(triggerName, DbSchemas.UserRelationOperation.TABLE_NAME)
			.setAction(TriggerGenerator.DatabaseAction.AFTER_INSERT)
			.setWhen(
				new Template("new.${action} = '${action_name}'")
					.var("action", DbSchemas.UserRelationOperation.ACTION)
					.var("action_name", action.name())
					.render()
			).setStatements(
				new Template(
					"delete from ${u_feed} where ${u_handle} = new.${u_handle}" +
					" and ${feed_type} = '${ft_value}' and (${q_handle} = '' or ${q_handle} = new.${owner})")
					.var("u_feed", DbSchemas.UserFeeds.TABLE_NAME)
					.var("u_handle", DbSchemas.UserFeeds.USER_HANDLE)
					.var("feed_type", DbSchemas.UserFeeds.FEED_TYPE)
					.var("ft_value", feedType.name())
					.var("q_handle", DbSchemas.UserFeeds.QUERIED_USER_HANDLE)
					.var("owner", DbSchemas.UserRelationOperation.OWNER_HANDLE)
					.render()
			).build();
	}

	private static ISqlTrigger generateCleanupTrigger(String triggerName,
		UserCache.UserRelationAction firstAction, UserCache.UserRelationAction secondAction) {

		return new TriggerGenerator.TriggerBuilder(triggerName, DbSchemas.UserRelationOperation.TABLE_NAME)
			.setAction(TriggerGenerator.DatabaseAction.BEFORE_INSERT)
			.setWhen(
				new Template("new.${action} = '${a1}' or new.${action} = '${a2}'")
					.var("action", DbSchemas.UserRelationOperation.ACTION)
					.var("a1", firstAction.name())
					.var("a2", secondAction.name())
					.render()
			).setStatements(
				new Template("delete from ${rel_op} where ${u_handle} = new.${u_handle} and (${action} = '${a1}' or ${action} = '${a2}')")
					.var("rel_op", DbSchemas.UserRelationOperation.TABLE_NAME)
					.var("u_handle", DbSchemas.UserRelationOperation.USER_HANDLE)
					.var("action", DbSchemas.UserRelationOperation.ACTION)
					.var("a1", firstAction.name())
					.var("a2", secondAction.name())
					.render()
			).build();
	}

	private static ISqlTrigger generateInsertUserToFeedTrigger(String triggerName, UserCache.UserRelationAction action,
		UserCache.UserFeedType feedType, boolean useQueriedHandle) {

		return new TriggerGenerator.TriggerBuilder(triggerName, DbSchemas.UserRelationOperation.TABLE_NAME)
			.setAction(TriggerGenerator.DatabaseAction.AFTER_INSERT)
			.setWhen(
				new Template("new.${action} = '${a_value}'")
					.var("action", DbSchemas.UserRelationOperation.ACTION)
					.var("a_value", action.name())
					.render()
			).setStatements(
				new Template(
					"insert into ${u_feeds} ( ${u_handle}, ${feed_type}, ${q_handle} )"
						+ " values ( new.${u_handle}, '${feed_type_value}', "
						+ (useQueriedHandle ? "new.${owner}" : "''") + " )")
					.var("u_feeds", DbSchemas.UserFeeds.TABLE_NAME)
					.var("u_handle", DbSchemas.UserFeeds.USER_HANDLE)
					.var("feed_type", DbSchemas.UserFeeds.FEED_TYPE)
					.var("q_handle", DbSchemas.UserFeeds.QUERIED_USER_HANDLE)
					.var("owner", DbSchemas.UserRelationOperation.OWNER_HANDLE)
					.var("feed_type_value", feedType.name())
					.render()
			).build();
	}

	private static ISqlTrigger generateUpdateUserStatusTrigger(String triggerName,
		UserCache.UserRelationAction action, FollowingStatus newStatus) {

		return new TriggerGenerator.TriggerBuilder(triggerName, DbSchemas.UserRelationOperation.TABLE_NAME)
			.setAction(TriggerGenerator.DatabaseAction.AFTER_INSERT)
			.setWhen(
				new Template("new.${action} = '${a_value}'")
					.var("action", DbSchemas.UserRelationOperation.ACTION)
					.var("a_value", action.name())
					.render()
			).setStatements(
				new Template("update ${users} set ${f_status} = ${status_value} where ${u_handle} = new.${u_handle}")
					.var("users", DbSchemas.CompactUserData.TABLE_NAME)
					.var("f_status", DbSchemas.CompactUserData.FOLLOWER_STATUS)
					.var("status_value", newStatus.toValue())
					.var("u_handle", DbSchemas.UserFeeds.USER_HANDLE)
					.render(),
				new Template("update ${user_profile} set ${f_status} = ${status_value} where ${u_handle} = new.${u_handle}")
					.var("user_profile", DbSchemas.UserProfile.TABLE_NAME)
					.var("f_status", DbSchemas.CompactUserData.FOLLOWER_STATUS)
					.var("status_value", newStatus.toValue())
					.var("u_handle", DbSchemas.UserFeeds.USER_HANDLE)
					.render()
			).build();
	}

	/**
	 * Triggers required:
	 *  ! when a user is blocked:
	 *      * delete similar actions (block/unblock)
	 *      * add him to blocked user feed
	 *      * update his status (following = blocked) in user cache
	 *  ! when a user is followed:
	 *      * delete similar actions (follow/unfollow)
	 *      * update his status (pending) in user cache
	 *  ! when a user is unfollowed:
	 *      * delete similar actions (follow/unfollow)
	 *      * remove him from followed user feed
	 *      * delete his topics from feeds
	 *      ? update his status in user cache
	 *  ! when a user is unblocked:
	 *      * delete similar actions (block/unblock)
	 *      * remove him from blocked user feed
	 *      * update his status (none) in user cache
	 *  ! when a user is accepted or rejected:
	 *      * delete similar actions (accept/reject)
	 *      * remove him from pending feed
	 *      ! filter network pending user feed
	 *  ! when a user is accepted:
	 *      * add him to follower feed
	 */
}
