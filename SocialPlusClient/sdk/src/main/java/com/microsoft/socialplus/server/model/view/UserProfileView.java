/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.view;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.microsoft.autorest.models.FollowerStatus;
import com.microsoft.autorest.models.FollowingStatus;
import com.microsoft.socialplus.data.storage.DbSchemas;
import com.microsoft.socialplus.server.model.UniqueItem;

import static com.microsoft.socialplus.data.storage.DbSchemas.UserProfile;

/**
 *
 */
@DatabaseTable(tableName = UserProfile.TABLE_NAME)
public class UserProfileView implements UniqueItem {

	@DatabaseField(id = true, columnName = UserProfile.USER_HANDLE)
	private String userHandle;

	@DatabaseField
	private String username;

	@DatabaseField
	private String firstName;

	@DatabaseField
	private String lastName;

	@DatabaseField
	private String userPhotoUrl;

	@DatabaseField
	private String bio;

	@DatabaseField
	private String website;

	@DatabaseField
	private boolean isPrivate;

	@DatabaseField
	private int totalTopics;

	@DatabaseField
	private int totalFollowers;

	@DatabaseField
	private int totalFollowings;

	@DatabaseField(columnName = DbSchemas.UserProfile.FOLLOWER_STATUS)
	private String followerStatus;

	@DatabaseField(columnName = DbSchemas.UserProfile.FOLLOWING_STATUS)
	private String followingStatus;

	/**
	 * For ORM.
	 */
	UserProfileView() {
	}

	public String getHandle() {
		return userHandle;
	}

	public String getUsername() {
		return username;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getUserPhotoUrl() {
		return userPhotoUrl;
	}

	public String getUserCoverUrl() {
		// TODO Debug value. Update after API change
		return userPhotoUrl;
	}

	public String getBio() {
		return bio;
	}

	public String getWebsite() {
		return website;
	}

	public boolean isPrivate() {
		return isPrivate;
	}

	public int getTotalTopics() {
		return totalTopics;
	}

	public int getTotalFollowers() {
		return totalFollowers;
	}

	public int getTotalFollowings() {
		return totalFollowings;
	}

	public FollowerStatus getFollowerStatus() {
		return FollowerStatus.valueOf(followerStatus);
	}

	public FollowingStatus getFollowingStatus() {
		return FollowingStatus.valueOf(followingStatus);
	}
}
