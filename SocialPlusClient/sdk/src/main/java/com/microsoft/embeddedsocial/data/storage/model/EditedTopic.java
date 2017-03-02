/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.microsoft.embeddedsocial.data.storage.DbSchemas;

/**
 * Represents an edited topic.
 */
@DatabaseTable(tableName = DbSchemas.EditedTopic.TABLE_NAME)
public class EditedTopic {

	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField(columnName = DbSchemas.Topics.TOPIC_HANDLE)
	private String topicHandle;

	@DatabaseField(columnName = DbSchemas.Topics.TOPIC_TITLE)
	private String topicTitle;

	@DatabaseField(columnName = DbSchemas.Topics.TOPIC_TEXT)
	private String topicText;

	//TODO make database field
	private String topicCategories;

	/**
	 * For ORM.
	 */
	EditedTopic() {  }

	public EditedTopic(String topicHandle, String topicTitle, String topicText, String topicCategories) {
		this.topicHandle = topicHandle;
		this.topicTitle = topicTitle;
		this.topicText = topicText;
		this.topicCategories = topicCategories;
	}

	public String getTopicHandle() {
		return topicHandle;
	}

	public String getTopicText() {
		return topicText;
	}

	public String getTopicTitle() {
		return topicTitle;
	}

	public String getTopicCategories() {
		return topicCategories;
	}
}
