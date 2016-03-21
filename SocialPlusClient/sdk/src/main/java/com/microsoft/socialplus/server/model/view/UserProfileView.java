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
import com.microsoft.autorest.models.Visibility;
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

	public UserProfileView(com.microsoft.autorest.models.UserProfileView view) {
		userHandle = view.getUserHandle();
//		username =
		firstName = view.getFirstName();
		lastName = view.getLastName();
		userPhotoUrl = view.getPhotoUrl();
		bio = view.getBio();
//		website =
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
