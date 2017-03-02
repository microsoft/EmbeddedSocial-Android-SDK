/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.microsoft.embeddedsocial.data.storage.DbSchemas;

/**
 * Stores comment-feed relation.
 */
@SuppressWarnings("unused")
@DatabaseTable(tableName = DbSchemas.CommentFeedRelation.TABLE_NAME)
public class CommentFeedRelation {

	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField(columnName = DbSchemas.CommentFeedRelation.FEED_TYPE)
	private int feedType;

	@DatabaseField(columnName = DbSchemas.CommentFeedRelation.TOPIC_HANDLE)
	private String topicHandle;

	@DatabaseField(columnName = DbSchemas.CommentFeedRelation.COMMENT_HANDLE)
	private String commentHandle;

	/**
	 * For ORM.
	 */
	CommentFeedRelation() {  }

	public CommentFeedRelation(int feedType, String topicHandle, String commentHandle) {
		this.feedType = feedType;
		this.topicHandle = topicHandle;
		this.commentHandle = commentHandle;
	}

	public String getCommentHandle() {
		return commentHandle;
	}

	public int getFeedType() {
		return feedType;
	}

	public String getTopicHandle() {
		return topicHandle;
	}
}
