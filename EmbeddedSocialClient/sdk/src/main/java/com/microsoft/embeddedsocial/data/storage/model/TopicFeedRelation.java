/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.microsoft.embeddedsocial.data.storage.DbSchemas;

/**
 * Stores topic-feed relation.
 */
@SuppressWarnings("unused")
@DatabaseTable(tableName = DbSchemas.TopicFeedRelation.TABLE_NAME)
public class TopicFeedRelation {

	public static final String DEFAULT_QUERY = "";

	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField(columnName = DbSchemas.TopicFeedRelation.FEED_TYPE)
	private int topicFeedType;

	@DatabaseField(columnName = DbSchemas.TopicFeedRelation.QUERY)
	private String query;

	@DatabaseField(columnName = DbSchemas.TopicFeedRelation.TOPIC_HANDLE)
	private String topicHandle;

	TopicFeedRelation() {  }

	public TopicFeedRelation(String query, int topicFeedType, String topicHandle) {
		this.query = query != null ? query : DEFAULT_QUERY;
		this.topicFeedType = topicFeedType;
		this.topicHandle = topicHandle;
	}

	public String getQuery() {
		return query;
	}

	public int getTopicFeedType() {
		return topicFeedType;
	}

	public String getTopicHandle() {
		return topicHandle;
	}
}
