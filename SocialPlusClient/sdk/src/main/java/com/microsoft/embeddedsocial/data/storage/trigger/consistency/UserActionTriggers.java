/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage.trigger.consistency;

import com.microsoft.embeddedsocial.base.expression.Template;
import com.microsoft.embeddedsocial.data.model.TopicFeedType;
import com.microsoft.embeddedsocial.data.storage.DbSchemas;
import com.microsoft.embeddedsocial.data.storage.trigger.ISqlTrigger;
import com.microsoft.embeddedsocial.autorest.models.ContentType;
import com.microsoft.embeddedsocial.data.storage.trigger.TriggerGenerator;

/**
 * Cache consistency triggers for user actions.
 */
public class UserActionTriggers {

	private static final String LIKE_TOPIC_TRIGGER_BODY
		= "update " + DbSchemas.Topics.TABLE_NAME
		+ "\r\nset " + DbSchemas.Topics.LIKE_STATUS
		+ " = new." + DbSchemas.LikeStatus.STATUS
		+ ",\r\n" + DbSchemas.Topics.TOTAL_LIKES
		+ " = case when new." + DbSchemas.LikeStatus.STATUS
		+ " then " + DbSchemas.Topics.TOTAL_LIKES + " + 1"
		+ " else " + DbSchemas.Topics.TOTAL_LIKES + " - 1"
		+ " end"
		+ "\r\nwhere " + DbSchemas.Topics.TOPIC_HANDLE
		+ " = new." + DbSchemas.LikeStatus.CONTENT_HANDLE;

	private static final String LIKE_COMMENT_TRIGGER_BODY
		= "update " + DbSchemas.Comments.TABLE_NAME
		+ "\r\nset " + DbSchemas.Comments.LIKE_STATUS
		+ " = new." + DbSchemas.LikeStatus.STATUS
		+ ",\r\n" + DbSchemas.Comments.TOTAL_LIKES
		+ " = case when new." + DbSchemas.LikeStatus.STATUS
		+ " then " + DbSchemas.Comments.TOTAL_LIKES + " + 1"
		+ " else " + DbSchemas.Comments.TOTAL_LIKES + " - 1"
		+ " end"
		+ "\r\nwhere " + DbSchemas.Comments.COMMENT_HANDLE
		+ " = new." + DbSchemas.LikeStatus.CONTENT_HANDLE;

	private static final String LIKE_REPLY_TRIGGER_BODY
		= "update " + DbSchemas.Replies.TABLE_NAME
		+ "\r\nset " + DbSchemas.Replies.LIKE_STATUS
		+ " = new." + DbSchemas.LikeStatus.STATUS
		+ ",\r\n" + DbSchemas.Replies.TOTAL_LIKES
		+ " = case when new." + DbSchemas.LikeStatus.STATUS
		+ " then " + DbSchemas.Replies.TOTAL_LIKES + " + 1"
		+ " else " + DbSchemas.Replies.TOTAL_LIKES + " - 1"
		+ " end"
		+ "\r\nwhere " + DbSchemas.Replies.REPLY_HANDLE
		+ " = new." + DbSchemas.LikeStatus.CONTENT_HANDLE;

	/**
	 * All SQL triggers contained in this class.
	 */
	public static final ISqlTrigger[] TRIGGERS = {

		TriggerGenerator.newOnBeforeInsertTrigger(
			"pin_cleanup",
			DbSchemas.PinStatus.TABLE_NAME,
			new Template("delete from ${pins} where ${handle} = new.${handle}")
				.var("pins", DbSchemas.PinStatus.TABLE_NAME)
				.var("handle", DbSchemas.PinStatus.TOPIC_HANDLE)
				.render()
		),

		TriggerGenerator.newOnBeforeInsertTrigger(
			"like_cleanup",
			DbSchemas.LikeStatus.TABLE_NAME,
			new Template("delete from ${likes} where ${handle} = new.${handle} and ${type} = new.${type}")
				.var("likes", DbSchemas.LikeStatus.TABLE_NAME)
				.var("handle", DbSchemas.LikeStatus.CONTENT_HANDLE)
				.var("type", DbSchemas.LikeStatus.CONTENT_TYPE)
				.render()
		),

		TriggerGenerator.newOnAfterInsertTrigger(
			"pin_consistency",
			DbSchemas.PinStatus.TABLE_NAME,
			new Template(
				"update ${topics} " +
					"set ${t_pinstatus} = new.${status} where ${topics}.${t_handle} = new.${t_handle}")
				.var("topics", DbSchemas.Topics.TABLE_NAME)
				.var("t_pinstatus", DbSchemas.Topics.PIN_STATUS)
				.var("status", DbSchemas.PinStatus.PIN_STATUS)
				.var("t_handle", DbSchemas.Topics.TOPIC_HANDLE)
				.render()
		),

		generateLikeConsistencyTrigger(
			"like_consistency_topics",
			ContentType.TOPIC,
			LIKE_TOPIC_TRIGGER_BODY
		),

		generateLikeConsistencyTrigger(
			"like_consistency_comments",
			ContentType.COMMENT,
			LIKE_COMMENT_TRIGGER_BODY
		),

		generateLikeConsistencyTrigger(
			"like_consistency_replies",
			ContentType.REPLY,
			LIKE_REPLY_TRIGGER_BODY
		),

		new TriggerGenerator.TriggerBuilder("report_content_cleanup", DbSchemas.ReportContentOperation.TABLE_NAME)
			.setAction(TriggerGenerator.DatabaseAction.BEFORE_INSERT)
			.setStatements(
				new Template("delete from ${ops} where ${handle} = new.${handle} and ${type} = new.${type}")
					.var("ops", DbSchemas.ReportContentOperation.TABLE_NAME)
					.var("handle", DbSchemas.ReportContentOperation.CONTENT_HANDLE)
					.var("type", DbSchemas.ReportContentOperation.CONTENT_TYPE)
					.render()
			).build(),

		TriggerGenerator.newOnBeforeInsertTrigger(
			"hide_topic_cleanup",
			DbSchemas.HideTopicAction.TABLE_NAME,
			new Template("delete from ${hidden_topics} where ${t_handle} = new.${t_handle}")
				.var("hidden_topics", DbSchemas.HideTopicAction.TABLE_NAME)
				.var("t_handle", DbSchemas.Topics.TOPIC_HANDLE)
				.render()
		),

		TriggerGenerator.newOnAfterInsertTrigger(
			"hide_topic_consistency",
			DbSchemas.HideTopicAction.TABLE_NAME,
			new Template("delete from ${t_feeds} where ${t_handle} = new.${t_handle} and ${feed_type} = ${feed_type_value}")
				.var("t_feeds", DbSchemas.TopicFeedRelation.TABLE_NAME)
				.var("t_handle", DbSchemas.TopicFeedRelation.TOPIC_HANDLE)
				.var("feed_type", DbSchemas.TopicFeedRelation.FEED_TYPE)
				.var("feed_type_value", TopicFeedType.FOLLOWING_RECENT.ordinal())
				.render()
		)
	};

	private static ISqlTrigger generateLikeConsistencyTrigger(String triggerName,
		ContentType contentType, String body) {

		return new TriggerGenerator.TriggerBuilder(triggerName, DbSchemas.LikeStatus.TABLE_NAME)
			.setAction(TriggerGenerator.DatabaseAction.AFTER_INSERT)
			.setWhen(
				new Template("new.${type} = '${type_value}'")
					.var("type", DbSchemas.LikeStatus.CONTENT_TYPE)
					.var("type_value", contentType.name())
					.render()
			).setStatements(body)
			.build();
	}
}
