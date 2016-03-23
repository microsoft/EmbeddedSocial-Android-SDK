/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.data.storage.trigger.consistency;

import com.microsoft.socialplus.autorest.models.ContentType;
import com.microsoft.socialplus.base.expression.Template;
import com.microsoft.socialplus.data.model.TopicFeedType;
import com.microsoft.socialplus.data.storage.DbSchemas;
import com.microsoft.socialplus.data.storage.trigger.ISqlTrigger;
import com.microsoft.socialplus.data.storage.trigger.TriggerGenerator;

import static com.microsoft.socialplus.data.storage.DbSchemas.Comments;
import static com.microsoft.socialplus.data.storage.DbSchemas.HideTopicAction;
import static com.microsoft.socialplus.data.storage.DbSchemas.LikeStatus;
import static com.microsoft.socialplus.data.storage.DbSchemas.PinStatus;
import static com.microsoft.socialplus.data.storage.DbSchemas.Replies;
import static com.microsoft.socialplus.data.storage.DbSchemas.ReportContentOperation;
import static com.microsoft.socialplus.data.storage.DbSchemas.Topics;

/**
 * Cache consistency triggers for user actions.
 */
public class UserActionTriggers {

	private static final String LIKE_TOPIC_TRIGGER_BODY
		= "update " + Topics.TABLE_NAME
		+ "\r\nset " + Topics.LIKE_STATUS
		+ " = new." + LikeStatus.STATUS
		+ ",\r\n" + Topics.TOTAL_LIKES
		+ " = case when new." + LikeStatus.STATUS
		+ " then " + Topics.TOTAL_LIKES + " + 1"
		+ " else " + Topics.TOTAL_LIKES + " - 1"
		+ " end"
		+ "\r\nwhere " + Topics.TOPIC_HANDLE
		+ " = new." + LikeStatus.CONTENT_HANDLE;

	private static final String LIKE_COMMENT_TRIGGER_BODY
		= "update " + Comments.TABLE_NAME
		+ "\r\nset " + Comments.LIKE_STATUS
		+ " = new." + LikeStatus.STATUS
		+ ",\r\n" + Comments.TOTAL_LIKES
		+ " = case when new." + LikeStatus.STATUS
		+ " then " + Comments.TOTAL_LIKES + " + 1"
		+ " else " + Comments.TOTAL_LIKES + " - 1"
		+ " end"
		+ "\r\nwhere " + Comments.COMMENT_HANDLE
		+ " = new." + LikeStatus.CONTENT_HANDLE;

	private static final String LIKE_REPLY_TRIGGER_BODY
		= "update " + Replies.TABLE_NAME
		+ "\r\nset " + Replies.LIKE_STATUS
		+ " = new." + LikeStatus.STATUS
		+ ",\r\n" + Replies.TOTAL_LIKES
		+ " = case when new." + LikeStatus.STATUS
		+ " then " + Replies.TOTAL_LIKES + " + 1"
		+ " else " + Replies.TOTAL_LIKES + " - 1"
		+ " end"
		+ "\r\nwhere " + Replies.REPLY_HANDLE
		+ " = new." + LikeStatus.CONTENT_HANDLE;

	/**
	 * All SQL triggers contained in this class.
	 */
	public static final ISqlTrigger[] TRIGGERS = {

		TriggerGenerator.newOnBeforeInsertTrigger(
			"pin_cleanup",
			PinStatus.TABLE_NAME,
			new Template("delete from ${pins} where ${handle} = new.${handle}")
				.var("pins", PinStatus.TABLE_NAME)
				.var("handle", PinStatus.TOPIC_HANDLE)
				.render()
		),

		TriggerGenerator.newOnBeforeInsertTrigger(
			"like_cleanup",
			LikeStatus.TABLE_NAME,
			new Template("delete from ${likes} where ${handle} = new.${handle} and ${type} = new.${type}")
				.var("likes", LikeStatus.TABLE_NAME)
				.var("handle", LikeStatus.CONTENT_HANDLE)
				.var("type", LikeStatus.CONTENT_TYPE)
				.render()
		),

		TriggerGenerator.newOnAfterInsertTrigger(
			"pin_consistency",
			PinStatus.TABLE_NAME,
			new Template(
				"update ${topics} " +
					"set ${t_pinstatus} = new.${status} where ${topics}.${t_handle} = new.${t_handle}")
				.var("topics", Topics.TABLE_NAME)
				.var("t_pinstatus", Topics.PIN_STATUS)
				.var("status", PinStatus.PIN_STATUS)
				.var("t_handle", Topics.TOPIC_HANDLE)
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

		new TriggerGenerator.TriggerBuilder("report_content_cleanup", ReportContentOperation.TABLE_NAME)
			.setAction(TriggerGenerator.DatabaseAction.BEFORE_INSERT)
			.setStatements(
				new Template("delete from ${ops} where ${handle} = new.${handle} and ${type} = new.${type}")
					.var("ops", ReportContentOperation.TABLE_NAME)
					.var("handle", ReportContentOperation.CONTENT_HANDLE)
					.var("type", ReportContentOperation.CONTENT_TYPE)
					.render()
			).build(),

		TriggerGenerator.newOnBeforeInsertTrigger(
			"hide_topic_cleanup",
			HideTopicAction.TABLE_NAME,
			new Template("delete from ${hidden_topics} where ${t_handle} = new.${t_handle}")
				.var("hidden_topics", HideTopicAction.TABLE_NAME)
				.var("t_handle", Topics.TOPIC_HANDLE)
				.render()
		),

		TriggerGenerator.newOnAfterInsertTrigger(
			"hide_topic_consistency",
			HideTopicAction.TABLE_NAME,
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

		return new TriggerGenerator.TriggerBuilder(triggerName, LikeStatus.TABLE_NAME)
			.setAction(TriggerGenerator.DatabaseAction.AFTER_INSERT)
			.setWhen(
				new Template("new.${type} = '${type_value}'")
					.var("type", LikeStatus.CONTENT_TYPE)
					.var("type_value", contentType.name())
					.render()
			).setStatements(body)
			.build();
	}
}
