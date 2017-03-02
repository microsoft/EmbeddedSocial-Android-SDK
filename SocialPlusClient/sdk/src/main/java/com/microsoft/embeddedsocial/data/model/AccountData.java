/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.microsoft.embeddedsocial.server.model.view.ThirdPartyAccountView;
import com.microsoft.embeddedsocial.autorest.models.FollowerStatus;
import com.microsoft.embeddedsocial.autorest.models.IdentityProvider;
import com.microsoft.embeddedsocial.image.ImageLocation;
import com.microsoft.embeddedsocial.server.model.view.UserAccountView;
import com.microsoft.embeddedsocial.server.model.view.UserProfileView;

import java.util.List;

/**
 * Information about some user's account.
 */
public class AccountData implements Parcelable {

	private IdentityProvider identityProvider;
	private String firstName;
	private String lastName;
	private String userPhotoUrl;
	private String bio;
	private String thirdPartyAccountHandle;
	private String thirdPartyAccessToken;
	private long followersCount;
	private long followingCount;
	private boolean isPrivate;
	private FollowerStatus followedStatus = FollowerStatus.NONE;

	public AccountData() {
		identityProvider = IdentityProvider.MICROSOFT; // TODO verify this default value is OK
	}

	/**
	 * Creates a new instance from a response of /GetUserProfile server method.
	 */
	public AccountData(UserProfileView userProfile) {
		this();
		this.firstName = userProfile.getFirstName();
		this.lastName = userProfile.getLastName();
		this.userPhotoUrl = userProfile.getUserPhotoUrl();
		this.bio = userProfile.getBio();
		this.followingCount = userProfile.getTotalFollowings();
		this.followersCount = userProfile.getTotalFollowers();
		this.isPrivate = userProfile.isPrivate();
		FollowerStatus status = userProfile.getFollowerStatus();
		this.followedStatus = status != null ? status : FollowerStatus.NONE;
	}

	private AccountData(Parcel in) {
		this.identityProvider = IdentityProvider.fromValue(in.readString());
		this.firstName = in.readString();
		this.lastName = in.readString();
		this.userPhotoUrl = in.readString();
		this.bio = in.readString();
		this.thirdPartyAccountHandle = in.readString();
		this.thirdPartyAccessToken = in.readString();
		this.followersCount = in.readLong();
		this.followingCount = in.readLong();
		this.isPrivate = in.readByte() != 0;
		FollowerStatus status = FollowerStatus.fromValue(in.readString());
		this.followedStatus = status != null ? status : FollowerStatus.NONE;
	}

	/**
	 * Whether we have rights to obtain the feeds of popular and recent user's topics.
	 */
	public boolean arePostsReadable() {
		return followedStatus != FollowerStatus.BLOCKED && (!isPrivate || followedStatus == FollowerStatus.FOLLOW);
	}

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUserPhotoUrl() {
		return userPhotoUrl;
	}

	public ImageLocation getUserPhotoLocation() {
		return ImageLocation.createUserPhotoImageLocation(userPhotoUrl);
	}

	public void setUserPhotoUrl(String userPhotoUrl) {
		this.userPhotoUrl = userPhotoUrl;
	}

	public String getFullName() {
		return firstName + ' ' + lastName;
	}

	public IdentityProvider getIdentityProvider() {
		return identityProvider;
	}

	public void setIdentityProvider(IdentityProvider identityProvider) {
		this.identityProvider = identityProvider;
	}

	public String getThirdPartyAccountHandle() {
		return thirdPartyAccountHandle;
	}

	public void setThirdPartyAccountHandle(String thirdPartyAccountHandle) {
		this.thirdPartyAccountHandle = thirdPartyAccountHandle;
	}

	public String getThirdPartyAccessToken() {
		return thirdPartyAccessToken;
	}

	public void setThirdPartyAccessToken(String thirdPartyAccessToken) {
		this.thirdPartyAccessToken = thirdPartyAccessToken;
	}

	public long getFollowersCount() {
		return followersCount;
	}

	public void setFollowersCount(long followersCount) {
		this.followersCount = followersCount;
	}

	public long getFollowingCount() {
		return followingCount;
	}

	public void setFollowingCount(long followingCount) {
		this.followingCount = followingCount;
	}

	public boolean isPrivate() {
		return isPrivate;
	}

	public void setIsPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}

	public FollowerStatus getFollowedStatus() {
		return followedStatus;
	}

	public void setFollowedStatus(FollowerStatus followedStatus) {
		this.followedStatus = followedStatus;
	}

	public void setAccountTypeFromThirdPartyAccounts(List<ThirdPartyAccountView> thirdPartyAccounts) {
		if (thirdPartyAccounts == null || thirdPartyAccounts.isEmpty()) {
			throw new IllegalArgumentException("Third party accounts list should not be null");
		}
		identityProvider = thirdPartyAccounts.get(0).getIdentityProvider();
	}

	/**
	 * Creates a new instance from a response of /GetUserAccount server method.
	 */
	public static AccountData fromServerResponse(UserAccountView userAccountView) {
		AccountData accountData = new AccountData();
		accountData.setFirstName(userAccountView.getFirstName());
		accountData.setLastName(userAccountView.getLastName());
		accountData.setUserPhotoUrl(userAccountView.getUserPhotoUrl());
		accountData.setBio(userAccountView.getBio());
		accountData.setIsPrivate(userAccountView.isPrivate());
		accountData.setAccountTypeFromThirdPartyAccounts(userAccountView.getThirdPartyAccounts());
		return accountData;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.identityProvider.toValue());
		dest.writeString(this.firstName);
		dest.writeString(this.lastName);
		dest.writeString(this.userPhotoUrl);
		dest.writeString(this.bio);
		dest.writeString(this.thirdPartyAccountHandle);
		dest.writeString(this.thirdPartyAccessToken);
		dest.writeLong(this.followersCount);
		dest.writeLong(this.followingCount);
		dest.writeByte(isPrivate ? (byte) 1 : (byte) 0);
		dest.writeString((this.followedStatus == null ? FollowerStatus.NONE : this.followedStatus).toValue());
	}

	public static final Creator<AccountData> CREATOR = new Creator<AccountData>() {
		public AccountData createFromParcel(Parcel source) {
			return new AccountData(source);
		}

		public AccountData[] newArray(int size) {
			return new AccountData[size];
		}
	};
}
