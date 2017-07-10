/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.microsoft.embeddedsocial.account.UserAccount;
import com.microsoft.embeddedsocial.data.storage.DbSchemas;
import com.microsoft.embeddedsocial.data.storage.UserCache;

/**
 * An operation related to user relationships such as following, unfollowing, etc.
 */
@SuppressWarnings("unused")
@DatabaseTable(tableName = DbSchemas.UserRelationOperation.TABLE_NAME)
public class UserRelationOperation {

	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField(columnName = DbSchemas.UserRelationOperation.OWNER_HANDLE)
	private String ownerHandle;

	@DatabaseField(columnName = DbSchemas.UserRelationOperation.USER_HANDLE)
	private String targetUserHandle;

	@DatabaseField(columnName = DbSchemas.UserRelationOperation.ACTION)
	private UserCache.UserRelationAction action;

	/**
	 * For ORM.
	 */
	UserRelationOperation() {
	}

	public UserRelationOperation(UserCache.UserRelationAction action, String targetUserHandle) {
		this.action = action;
		this.targetUserHandle = targetUserHandle;
		this.ownerHandle = UserAccount.getInstance().getUserHandle();
	}

	public UserCache.UserRelationAction getAction() {
		return action;
	}

	public String getTargetUserHandle() {
		return targetUserHandle;
	}
}
