/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.microsoft.embeddedsocial.server.model.view.UserCompactView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Encapsulates a follow request.
 */
public class FollowRequest implements Parcelable {

	/**
	 * Status of follow request.
	 */
	public enum Status {
		PENDING, // follow request is not accepted or rejected yet
		ACCEPTED,
		REJECTED
	}

	/**
	 * The user sent this follow request.
	 */
	private final UserCompactView user;
	private Status status;

	public FollowRequest(UserCompactView user) {
		this.user = user;
		this.status = Status.PENDING;
	}

	private FollowRequest(Parcel in) {
		this.user = in.readParcelable(UserCompactView.class.getClassLoader());
		int tmpStatus = in.readInt();
		this.status = tmpStatus == -1 ? null : Status.values()[tmpStatus];
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public UserCompactView getUser() {
		return user;
	}

	/**
	 * Wraps a collection of users requested to follow you (obtained by /GetUserPendingFeed server method) into a collection of corresponding
	 * {@link FollowRequest} objects with {@link FollowRequest.Status#PENDING} status.
	 */
	public static List<FollowRequest> wrap(List<UserCompactView> users) {
		if (users == null) {
			return Collections.emptyList();
		}
		List<FollowRequest> requests = new ArrayList<>(users.size());
		for (UserCompactView user : users) {
			requests.add(new FollowRequest(user));
		}
		return requests;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(this.user, 0);
		dest.writeInt(this.status == null ? -1 : this.status.ordinal());
	}

	public static final Parcelable.Creator<FollowRequest> CREATOR = new Parcelable.Creator<FollowRequest>() {
		public FollowRequest createFromParcel(Parcel source) {
			return new FollowRequest(source);
		}

		public FollowRequest[] newArray(int size) {
			return new FollowRequest[size];
		}
	};
}
