/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage.trigger.consistency;

import com.microsoft.embeddedsocial.data.storage.DbSchemas;
import com.microsoft.embeddedsocial.data.storage.trigger.ISqlTrigger;
import com.microsoft.embeddedsocial.autorest.models.ContentType;
import com.microsoft.embeddedsocial.base.expression.Template;
import com.microsoft.embeddedsocial.data.storage.trigger.TriggerGenerator;

/**
 * Cache consistency triggers for topics/comments/replies.
 */
public class ContentTriggers {

	private static final String CLEANUP_COMMENT_FEEDS_STATEMENT
		= "delete from " + DbSchemas.CommentFeedRelation.TABLE_NAME
		+ " where OLD." + DbSchemas.Topics.TOPIC_HANDLE + " = "
		+ DbSchemas.CommentFeedRelation.TABLE_NAME + "." + DbSchemas.CommentFeedRelation.TOPIC_HANDLE;

	private static final String DELETE_UNREFERENCED_TOPICS_STATEMENT
		= "delete from " + DbSchemas.Topics.TABLE_NAME
		+ " where not exists (select 1 from "
		+ DbSchemas.TopicFeedRelation.TABLE_NAME + " as t"
		+ " where " + DbSchemas.Topics.TABLE_NAME + "." + DbSchemas.Topics.TOPIC_HANDLE
		+ " = t." + DbSchemas.TopicFeedRelation.TOPIC_HANDLE + ")";

	private static final String DELETE_UNREFERENCED_COMMENTS_STATEMENT
		= "delete from " + DbSchemas.Comments.TABLE_NAME
		+ " where OLD." + DbSchemas.Topics.TOPIC_HANDLE + " = "
		+ DbSchemas.Comments.TABLE_NAME + "." + DbSchemas.Comments.TOPIC_HANDLE;

	private static final String DELETE_UNREFERENCED_REPLIES_STATEMENT
		= "delete from " + DbSchemas.Replies.TABLE_NAME
		+ " where OLD." + DbSchemas.Comments.COMMENT_HANDLE + " = "
		+ DbSchemas.Replies.TABLE_NAME + "." + DbSchemas.Replies.COMMENT_HANDLE;

	/**
	 * All SQL triggers contained in this class.
	 */
	public static final ISqlTrigger[] TRIGGERS = {

		TriggerGenerator.newOnAfterDeleteTrigger(       // TODO this can be optimized
			"topic_consistency",
			DbSchemas.TopicFeedRelation.TABLE_NAME,
			DELETE_UNREFERENCED_TOPICS_STATEMENT
		),

		TriggerGenerator.newOnAfterDeleteTrigger(
			"comment_feed_consistency",
			DbSchemas.Topics.TABLE_NAME,
			CLEANUP_COMMENT_FEEDS_STATEMENT
		),

		TriggerGenerator.newOnAfterDeleteTrigger(
			"comment_consistency",
			DbSchemas.Topics.TABLE_NAME,
			DELETE_UNREFERENCED_COMMENTS_STATEMENT
		),

		TriggerGenerator.newOnAfterDeleteTrigger(
			"reply_consistency",
			DbSchemas.Comments.TABLE_NAME,
			DELETE_UNREFERENCED_REPLIES_STATEMENT
		),

		new TriggerGenerator.TriggerBuilder("new_comment_consistency", DbSchemas.DiscussionItem.TABLE_NAME)
			.setAction(TriggerGenerator.DatabaseAction.AFTER_INSERT)
			.setWhen(
				new Template("new.${type} = '${type_value}'")
					.var("type", DbSchemas.DiscussionItem.CONTENT_TYPE)
					.var("type_value", ContentType.COMMENT.toValue())
					.render()
			).setStatements(
			new Template("update ${topics} set ${comment_count} = ${comment_count} + 1"
					+ " where ${t_handle} = new.${root_handle}")
				.var("topics", DbSchemas.Topics.TABLE_NAME)
				.var("comment_count", DbSchemas.Topics.TOTAL_COMMENTS)
				.var("t_handle", DbSchemas.Topics.TOPIC_HANDLE)
				.var("root_handle", DbSchemas.DiscussionItem.ROOT_HANDLE)
				.render()
			).build(),

		new TriggerGenerator.TriggerBuilder("new_reply_consistency", DbSchemas.DiscussionItem.TABLE_NAME)
			.setAction(TriggerGenerator.DatabaseAction.AFTER_INSERT)
			.setWhen(
				new Template("new.${type} = '${type_value}'")
					.var("type", DbSchemas.DiscussionItem.CONTENT_TYPE)
					.var("type_value", ContentType.REPLY.toValue())
					.render()
			).setStatements(
				new Template("update ${comments} set ${reply_count} = ${reply_count} + 1"
					+ " where ${c_handle} = new.${root_handle}")
					.var("comments", DbSchemas.Comments.TABLE_NAME)
					.var("reply_count", DbSchemas.Comments.TOTAL_REPLIES)
					.var("c_handle", DbSchemas.Comments.COMMENT_HANDLE)
					.var("root_handle", DbSchemas.DiscussionItem.ROOT_HANDLE)
					.render()
			).build(),

		new TriggerGenerator.TriggerBuilder("edit_topic_cleanup", DbSchemas.EditedTopic.TABLE_NAME)
			.setAction(TriggerGenerator.DatabaseAction.BEFORE_INSERT)
			.setStatements(
				new Template("delete from ${edited_topic} where ${handle} = new.${handle}")
					.var("edited_topic", DbSchemas.EditedTopic.TABLE_NAME)
					.var("handle", DbSchemas.Topics.TOPIC_HANDLE)
					.render()
			).build(),

		new TriggerGenerator.TriggerBuilder("edit_topic_update", DbSchemas.EditedTopic.TABLE_NAME)
			.setAction(TriggerGenerator.DatabaseAction.AFTER_INSERT)
			.setStatements(
				new Template("update ${topics} set ${title} = new.${title}, ${text} = new.${text}"
					+ " where ${handle} = new.${handle}")
					.var("handle", DbSchemas.Topics.TOPIC_HANDLE)
					.var("topics", DbSchemas.Topics.TABLE_NAME)
					.var("title", DbSchemas.Topics.TOPIC_TITLE)
					.var("text",  DbSchemas.Topics.TOPIC_TEXT)
					.render()
			).build(),
	};
}
