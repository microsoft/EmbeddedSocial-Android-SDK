/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.data.storage.trigger.consistency;

import com.microsoft.socialplus.autorest.models.ContentType;
import com.microsoft.socialplus.base.expression.Template;
import com.microsoft.socialplus.data.storage.DbSchemas;
import com.microsoft.socialplus.data.storage.trigger.ISqlTrigger;
import com.microsoft.socialplus.data.storage.trigger.TriggerGenerator;

import static com.microsoft.socialplus.data.storage.DbSchemas.CommentFeedRelation;
import static com.microsoft.socialplus.data.storage.DbSchemas.Comments;
import static com.microsoft.socialplus.data.storage.DbSchemas.Replies;
import static com.microsoft.socialplus.data.storage.DbSchemas.TopicFeedRelation;
import static com.microsoft.socialplus.data.storage.DbSchemas.Topics;

/**
 * Cache consistency triggers for topics/comments/replies.
 */
public class ContentTriggers {

	private static final String CLEANUP_COMMENT_FEEDS_STATEMENT
		= "delete from " + CommentFeedRelation.TABLE_NAME
		+ " where old." + Topics.TOPIC_HANDLE + " = "
		+ CommentFeedRelation.TABLE_NAME + "." + CommentFeedRelation.TOPIC_HANDLE;

	private static final String DELETE_UNREFERENCED_TOPICS_STATEMENT
		= "delete from " + Topics.TABLE_NAME
		+ " where not exists (select 1 from "
		+ TopicFeedRelation.TABLE_NAME + " as t"
		+ " where " + Topics.TABLE_NAME + "." + Topics.TOPIC_HANDLE
		+ " = t." + TopicFeedRelation.TOPIC_HANDLE + ")";

	private static final String DELETE_UNREFERENCED_COMMENTS_STATEMENT
		= "delete from " + Comments.TABLE_NAME
		+ " where OLD." + Topics.TOPIC_HANDLE + " = "
		+ Comments.TABLE_NAME + "." + Comments.TOPIC_HANDLE;

	private static final String DELETE_UNREFERENCED_REPLIES_STATEMENT
		= "delete from " + Replies.TABLE_NAME
		+ " where OLD." + Comments.COMMENT_HANDLE + " = "
		+ Replies.TABLE_NAME + "." + Replies.COMMENT_HANDLE;

	/**
	 * All SQL triggers contained in this class.
	 */
	public static final ISqlTrigger[] TRIGGERS = {

		TriggerGenerator.newOnAfterDeleteTrigger(       // TODO this can be optimized
			"topic_consistency",
			TopicFeedRelation.TABLE_NAME,
			DELETE_UNREFERENCED_TOPICS_STATEMENT
		),

		TriggerGenerator.newOnAfterDeleteTrigger(
			"comment_feed_consistency",
			Topics.TABLE_NAME,
			CLEANUP_COMMENT_FEEDS_STATEMENT
		),

		TriggerGenerator.newOnAfterDeleteTrigger(
			"comment_consistency",
			Topics.TABLE_NAME,
			DELETE_UNREFERENCED_COMMENTS_STATEMENT
		),

		TriggerGenerator.newOnAfterDeleteTrigger(
			"reply_consistency",
			Comments.TABLE_NAME,
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
				.var("topics", Topics.TABLE_NAME)
				.var("comment_count", Topics.TOTAL_COMMENTS)
				.var("t_handle", Topics.TOPIC_HANDLE)
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
					.var("comments", Comments.TABLE_NAME)
					.var("reply_count", Comments.TOTAL_REPLIES)
					.var("c_handle", Comments.COMMENT_HANDLE)
					.var("root_handle", DbSchemas.DiscussionItem.ROOT_HANDLE)
					.render()
			).build(),

		new TriggerGenerator.TriggerBuilder("edit_topic_cleanup", DbSchemas.EditedTopic.TABLE_NAME)
			.setAction(TriggerGenerator.DatabaseAction.BEFORE_INSERT)
			.setStatements(
				new Template("delete from ${edited_topic} where ${handle} = new.${handle}")
					.var("edited_topic", DbSchemas.EditedTopic.TABLE_NAME)
					.var("handle", Topics.TOPIC_HANDLE)
					.render()
			).build(),

		new TriggerGenerator.TriggerBuilder("edit_topic_update", DbSchemas.EditedTopic.TABLE_NAME)
			.setAction(TriggerGenerator.DatabaseAction.AFTER_INSERT)
			.setStatements(
				new Template("update ${topics} set ${title} = new.${title}, ${text} = new.${text}"
					+ " where ${handle} = new.${handle}")
					.var("handle", Topics.TOPIC_HANDLE)
					.var("topics", Topics.TABLE_NAME)
					.var("title", Topics.TOPIC_TITLE)
					.var("text",  Topics.TOPIC_TEXT)
					.render()
			).build(),
	};
}
