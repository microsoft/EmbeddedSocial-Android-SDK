/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model.view;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.microsoft.embeddedsocial.data.storage.DbSchemas;
import com.microsoft.embeddedsocial.autorest.models.*;
import com.microsoft.embeddedsocial.server.model.UniqueItem;

/**
 *
 */
@DatabaseTable(tableName = DbSchemas.CompactUserData.TABLE_NAME)
public class UserCompactView implements Parcelable, UniqueItem {

	@DatabaseField(id = true)
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
	private boolean isPrivate;

	@DatabaseField(columnName = DbSchemas.CompactUserData.FOLLOWER_STATUS)
	private String followerStatus;

	private boolean unblocked = false;

	/**
	 * Is used by ORM.
	 */
	@SuppressWarnings("unused")
	public UserCompactView() {
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

	public FollowerStatus getFollowerStatus() {
		return FollowerStatus.fromValue(followerStatus);
	}

	public void setFollowerStatus(FollowerStatus userRelationshipStatus) {
		followerStatus = userRelationshipStatus.toValue();
	}

	public String getFullName() {
		return firstName + " " + lastName;
	}

	public void setIsPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(userHandle);
		out.writeString(username);
		out.writeString(firstName);
		out.writeString(lastName);
		out.writeString(userPhotoUrl);
		out.writeInt(isPrivate ? 1 : 0);
		out.writeString(followerStatus);
	}

	private UserCompactView(Parcel in) {
		userHandle = in.readString();
		username = in.readString();
		firstName = in.readString();
		lastName = in.readString();
		userPhotoUrl = in.readString();
		isPrivate = in.readInt() == 1;
		followerStatus = in.readString();
	}

	public UserCompactView(com.microsoft.embeddedsocial.autorest.models.UserCompactView view) {
		userHandle = view.getUserHandle();
		firstName = view.getFirstName();
		lastName = view.getLastName();
		userPhotoUrl = view.getPhotoUrl();
		isPrivate = view.getVisibility() == Visibility.PRIVATE;
		followerStatus = view.getFollowerStatus().toValue();
	}

	public UserCompactView(com.microsoft.embeddedsocial.autorest.models.UserProfileView view) {
		userHandle = view.getUserHandle();
		firstName = view.getFirstName();
		lastName = view.getLastName();
		userPhotoUrl = view.getPhotoUrl();
		isPrivate = view.getVisibility() == Visibility.PRIVATE;
		followerStatus = view.getFollowerStatus().toValue();
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setUserHandle(String userHandle) {
		this.userHandle = userHandle;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setUserPhotoUrl(String userPhotoUrl) {
		this.userPhotoUrl = userPhotoUrl;
	}

	public boolean isUnblocked() {
		return unblocked;
	}

	public void setUnblocked(boolean unblocked) {
		this.unblocked = unblocked;
	}

	public boolean isPrivate() {
		return isPrivate;
	}

	public static final Parcelable.Creator<UserCompactView> CREATOR = new Parcelable.Creator<UserCompactView>() {
		public UserCompactView createFromParcel(Parcel in) {
			return new UserCompactView(in);
		}

		public UserCompactView[] newArray(int size) {
			return new UserCompactView[size];
		}
	};
}
