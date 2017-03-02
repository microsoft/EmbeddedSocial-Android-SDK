/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.microsoft.embeddedsocial.autorest.models.ContentType;
import com.microsoft.embeddedsocial.data.storage.DbSchemas;
import com.microsoft.embeddedsocial.server.model.view.CommentView;
import com.microsoft.embeddedsocial.server.model.view.ReplyView;

/**
 * Encapsulates data to add comments or replies.
 */
@DatabaseTable(tableName = DbSchemas.DiscussionItem.TABLE_NAME)
public class DiscussionItem {

	private static final String LOCAL_HANDLE_PREFIX = "__local__";

	@DatabaseField(generatedId = true, columnName = DbSchemas.DiscussionItem.ID)
	@SuppressWarnings("unused")
	private int id;

	@DatabaseField(columnName = DbSchemas.DiscussionItem.ROOT_HANDLE)
	private String rootHandle;

	@DatabaseField(columnName = DbSchemas.DiscussionItem.CONTENT_TEXT)
	private String contentText;

	@DatabaseField(columnName = DbSchemas.DiscussionItem.CONTENT_TYPE)
	private ContentType contentType;

	@DatabaseField(columnName = DbSchemas.DiscussionItem.IMAGE_PATH)
	private String imagePath;

	/**
	 * For ORM.
	 */
	@SuppressWarnings("unused")
	DiscussionItem() {  }

	private DiscussionItem(ContentType contentType, String rootHandle, String contentText, String imagePath) {
		this.contentType = contentType;
		this.rootHandle = rootHandle;
		this.contentText = contentText;
		this.imagePath = imagePath;
	}

	public static DiscussionItem newComment(String rootHandle, String contentText, String blobUrl) {
		return new DiscussionItem(ContentType.COMMENT, rootHandle, contentText, blobUrl);
	}

	public static DiscussionItem newReply(String rootHandle, String contentText) {
		return new DiscussionItem(ContentType.REPLY, rootHandle, contentText, null);
	}

	public String getRootHandle() {
		return rootHandle;
	}

	public String getContentText() {
		return contentText;
	}

	public ContentType getContentType() {
		return contentType;
	}

	public String getImagePath() {
		return imagePath;
	}

	public CommentView asComment() {
		CommentView commentView = new CommentView(rootHandle, LOCAL_HANDLE_PREFIX + hashCode(),
			getContentText(), getImagePath());
		commentView.setLocal(id);
		return commentView;
	}

	public ReplyView asReply() {
		ReplyView replyView = new ReplyView(rootHandle, LOCAL_HANDLE_PREFIX + hashCode(),
			getContentText());
		replyView.setLocal(id);
		return replyView;
	}
}
