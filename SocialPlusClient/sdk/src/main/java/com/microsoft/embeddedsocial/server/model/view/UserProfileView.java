/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model.view;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.microsoft.embeddedsocial.data.storage.DbSchemas;
import com.microsoft.embeddedsocial.server.model.UniqueItem;
import com.microsoft.embeddedsocial.autorest.models.FollowerStatus;
import com.microsoft.embeddedsocial.autorest.models.FollowingStatus;
import com.microsoft.embeddedsocial.autorest.models.Visibility;

/**
 *
 */
@DatabaseTable(tableName = DbSchemas.UserProfile.TABLE_NAME)
public class UserProfileView implements UniqueItem {

	@DatabaseField(id = true, columnName = DbSchemas.UserProfile.USER_HANDLE)
	private String userHandle;

	@DatabaseField
	private String firstName;

	@DatabaseField
	private String lastName;

	@DatabaseField
	private String userPhotoUrl;

	@DatabaseField
	private String bio;

	@DatabaseField
	private boolean isPrivate;

	@DatabaseField
	private long totalTopics;

	@DatabaseField
	private long totalFollowers;

	@DatabaseField
	private long totalFollowings;

	@DatabaseField(columnName = DbSchemas.UserProfile.FOLLOWER_STATUS)
	private String followerStatus;

	@DatabaseField(columnName = DbSchemas.UserProfile.FOLLOWING_STATUS)
	private String followingStatus;

	/**
	 * For ORM.
	 */
	UserProfileView() {
	}

	public UserProfileView(com.microsoft.embeddedsocial.autorest.models.UserProfileView view) {
		userHandle = view.getUserHandle();
		firstName = view.getFirstName();
		lastName = view.getLastName();
		userPhotoUrl = view.getPhotoUrl();
		bio = view.getBio();
		isPrivate = view.getVisibility() == Visibility.PRIVATE;
		totalTopics = view.getTotalTopics();
		totalFollowers = view.getTotalFollowers();
		totalFollowings = view.getTotalFollowing();
		followerStatus = view.getFollowerStatus().toValue();
		followingStatus = view.getFollowingStatus().toValue();
	}

	public String getHandle() {
		return userHandle;
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

	public String getBio() {
		return bio;
	}

	public boolean isPrivate() {
		return isPrivate;
	}

	public long getTotalTopics() {
		return totalTopics;
	}

	public long getTotalFollowers() {
		return totalFollowers;
	}

	public long getTotalFollowings() {
		return totalFollowings;
	}

	public FollowerStatus getFollowerStatus() {
		return FollowerStatus.fromValue(followerStatus);
	}

	public FollowingStatus getFollowingStatus() {
		return FollowingStatus.fromValue(followingStatus);
	}
}
