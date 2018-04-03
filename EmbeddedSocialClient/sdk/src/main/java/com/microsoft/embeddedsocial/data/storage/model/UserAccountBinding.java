/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.microsoft.embeddedsocial.data.storage.DbSchemas;

/**
 * Persistent binding between account and user.
 */
@SuppressWarnings("all")
@DatabaseTable(tableName = DbSchemas.UserAccountBinding.TABLE_NAME)
public class UserAccountBinding {

	@DatabaseField(id = true)
	private int id;

	@DatabaseField(columnName = DbSchemas.UserFeeds.USER_HANDLE)
	private String userHandle;

	@DatabaseField(columnName = DbSchemas.ThirdPartyAccount.ACCOUNT_HANDLE)
	private String accountHandle;

	/**
	 * For ORM.
	 */
	UserAccountBinding() {  }

	/**
	 * Creates an instance.
	 * @param userHandle        user handle
	 * @param accountHandle     account handle
	 */
	public UserAccountBinding(String userHandle, String accountHandle) {
		this.accountHandle = accountHandle;
		this.userHandle = userHandle;
	}

	public String getAccountHandle() {
		return accountHandle;
	}

	public String getUserHandle() {
		return userHandle;
	}
}
