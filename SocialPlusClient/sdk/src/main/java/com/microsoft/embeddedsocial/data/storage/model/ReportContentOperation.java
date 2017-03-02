/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.microsoft.embeddedsocial.data.storage.DbSchemas;
import com.microsoft.embeddedsocial.autorest.models.ContentType;
import com.microsoft.embeddedsocial.autorest.models.Reason;

/**
 * Represents persistent 'report content' operation.
 */
@DatabaseTable(tableName = DbSchemas.ReportContentOperation.TABLE_NAME)
public class ReportContentOperation {

	public static final String CONTENT_TYPE_USER = "USER";

	@SuppressWarnings("unused")
	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField(columnName = DbSchemas.ReportContentOperation.CONTENT_HANDLE)
	private String contentHandle;

	@DatabaseField(columnName = DbSchemas.ReportContentOperation.CONTENT_TYPE)
	private String contentType;

	@DatabaseField
	private String reason;

	/**
	 * For ORM.
	 */
	@SuppressWarnings("unused")
	ReportContentOperation() {  }

	private ReportContentOperation(String contentHandle, String contentType, Reason reason) {
		this.contentHandle = contentHandle;
		this.contentType = contentType;
		this.reason = reason.toValue();
	}

	/**
	 * Creates a new report content operation for specified content.
	 * @param contentHandle     content handle
	 * @param contentType       content type
	 * @param reason            reporting reason
	 * @return  {@linkplain ReportContentOperation} instance.
	 */
	public static ReportContentOperation forContent(String contentHandle, ContentType contentType,
	                                                Reason reason) {

		return new ReportContentOperation(contentHandle, contentType.toValue(), reason);
	}

	/**
	 * Creates a new report content operation for a user.
	 * @param userHandle    user handle
	 * @param reason        reporting reason
	 * @return  {@linkplain ReportContentOperation} instance.
	 */
	public static ReportContentOperation forUser(String userHandle, Reason reason) {
		return new ReportContentOperation(userHandle, CONTENT_TYPE_USER, reason);
}

	public String getContentHandle() {
		return contentHandle;
	}

	public String getContentType() {
		return contentType;
	}

	public Reason getReason() {
		return Reason.fromValue(reason);
	}

	public boolean isForUser() {
		return contentType.equals(CONTENT_TYPE_USER);
	}
}
