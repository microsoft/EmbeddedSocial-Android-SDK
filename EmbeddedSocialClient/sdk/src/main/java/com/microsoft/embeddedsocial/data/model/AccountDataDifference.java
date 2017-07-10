/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.model;

import android.net.Uri;
import android.os.Parcel;

/**
 * Encapsulates changes in account data. Used to track changes during account editing.
 */
public final class AccountDataDifference implements android.os.Parcelable {

	private boolean photoUriChanged = false;
	private boolean coverUriChanged = false;
	private Uri photoUri;
	private Uri coverUri;

	private boolean publicInfoChanged = false;
	private String firstName;
	private String lastName;
	private String bio;

	private boolean privacyChanged = false;
	private boolean isPrivate;

	public AccountDataDifference() {
	}

	private AccountDataDifference(Parcel in) {
		this.photoUriChanged = in.readByte() != 0;
		this.coverUriChanged = in.readByte() != 0;
		this.photoUri = in.readParcelable(Uri.class.getClassLoader());
		this.coverUri = in.readParcelable(Uri.class.getClassLoader());
		this.publicInfoChanged = in.readByte() != 0;
		this.firstName = in.readString();
		this.lastName = in.readString();
		this.bio = in.readString();
		this.privacyChanged = in.readByte() != 0;
		this.isPrivate = in.readByte() != 0;
	}

	/**
	 * Whether the object contains no changes.
	 */
	public boolean isEmpty() {
		return !(photoUriChanged | publicInfoChanged | privacyChanged);
	}

	/**
	 * Sets the Uri of new user's photo
	 * @param newPhotoUri local Uri to the new photo (may be null)
	 */
	public void setNewPhoto(Uri newPhotoUri) {
		photoUri = newPhotoUri;
		photoUriChanged = true;
	}

	public void setNewCover(Uri newCoverUri) {
		coverUri = newCoverUri;
		coverUriChanged = true;
	}

	/**
	 * Sets new user's name and bio
	 */
	public void setNewPublicInfo(String newFirstName, String newLastName, String newBio) {
		firstName = newFirstName;
		lastName = newLastName;
		bio = newBio;
		publicInfoChanged = true;
	}

	/**
	 * Sets whether a user wants to be private
	 */
	public void setNewPrivacy(boolean newIsPrivate) {
		isPrivate = newIsPrivate;
		privacyChanged = true;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getBio() {
		return bio;
	}

	public boolean isPrivate() {
		return isPrivate;
	}

	public Uri getPhotoUri() {
		return photoUri;
	}

	public Uri getCoverUri() {
		return coverUri;
	}

	/**
	 * Whether a new user's photo was set and a update request must be sent to the server.
	 */
	public boolean isPhotoUriChanged() {
		return photoUriChanged;
	}

	/**
	 * Whether a new cover image was set and a update request must be sent to the server.
	 */
	public boolean isCoverUriChanged() {
		return coverUriChanged;
	}

	/**
	 * Whether a user changed his privacy settings and a update request must be sent to the server.
	 */
	public boolean isPrivacyChanged() {
		return privacyChanged;
	}

	/**
	 * Whether new user's first name, last name or bio was set and a update request must be sent to the server.
	 */
	public boolean isPublicInfoChanged() {
		return publicInfoChanged;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeByte(photoUriChanged ? (byte) 1 : (byte) 0);
		dest.writeByte(coverUriChanged ? (byte) 1 : (byte) 0);
		dest.writeParcelable(this.photoUri, 0);
		dest.writeParcelable(this.coverUri, 0);
		dest.writeByte(publicInfoChanged ? (byte) 1 : (byte) 0);
		dest.writeString(this.firstName);
		dest.writeString(this.lastName);
		dest.writeString(this.bio);
		dest.writeByte(privacyChanged ? (byte) 1 : (byte) 0);
		dest.writeByte(isPrivate ? (byte) 1 : (byte) 0);
	}

	public static final Creator<AccountDataDifference> CREATOR = new Creator<AccountDataDifference>() {
		public AccountDataDifference createFromParcel(Parcel source) {
			return new AccountDataDifference(source);
		}

		public AccountDataDifference[] newArray(int size) {
			return new AccountDataDifference[size];
		}
	};
}
