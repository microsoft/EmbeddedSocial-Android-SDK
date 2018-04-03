/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage.model;

import android.text.TextUtils;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.microsoft.embeddedsocial.data.storage.DbSchemas;
import com.microsoft.embeddedsocial.data.storage.UserCache;

/**
 * Persistent relation binding users to user feed types.
 */
@SuppressWarnings("unused")
@DatabaseTable(tableName = DbSchemas.UserFeeds.TABLE_NAME)
public class UserFeedRelation {

	@DatabaseField(generatedId = true)
	private int id;

	@DatabaseField(columnName = DbSchemas.UserFeeds.USER_HANDLE)
	private String userHandle;

	@DatabaseField(columnName = DbSchemas.UserFeeds.FEED_TYPE)
	private UserCache.UserFeedType feedType;

	@DatabaseField(columnName = DbSchemas.UserFeeds.QUERIED_USER_HANDLE, defaultValue = UserCache.NO_HANDLE)
	private String queriedUserHandle;

	/**
	 * For ORM.
	 */
	UserFeedRelation() {
	}

	public UserFeedRelation(UserCache.UserFeedType feedType, String userHandle, String queriedUserHandle) {
		this.feedType = feedType;
		this.queriedUserHandle = !TextUtils.isEmpty(queriedUserHandle)
			? queriedUserHandle : UserCache.NO_HANDLE;
		this.userHandle = userHandle;
	}

	public String getQueriedUserHandle() {
		return queriedUserHandle;
	}

	public String getUserHandle() {
		return userHandle;
	}
}
