/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model;

import com.microsoft.socialplus.autorest.HashtagsOperations;
import com.microsoft.socialplus.autorest.HashtagsOperationsImpl;
import com.microsoft.socialplus.autorest.ImagesOperations;
import com.microsoft.socialplus.autorest.ImagesOperationsImpl;
import com.microsoft.socialplus.autorest.MyBlockedUsersOperations;
import com.microsoft.socialplus.autorest.MyBlockedUsersOperationsImpl;
import com.microsoft.socialplus.autorest.MyFollowersOperations;
import com.microsoft.socialplus.autorest.MyFollowersOperationsImpl;
import com.microsoft.socialplus.autorest.MyFollowingOperations;
import com.microsoft.socialplus.autorest.MyFollowingOperationsImpl;
import com.microsoft.socialplus.autorest.MyLinkedAccountsOperations;
import com.microsoft.socialplus.autorest.MyLinkedAccountsOperationsImpl;
import com.microsoft.socialplus.autorest.MyNotificationsOperations;
import com.microsoft.socialplus.autorest.MyNotificationsOperationsImpl;
import com.microsoft.socialplus.autorest.MyPendingUsersOperations;
import com.microsoft.socialplus.autorest.MyPendingUsersOperationsImpl;
import com.microsoft.socialplus.autorest.MyPinsOperations;
import com.microsoft.socialplus.autorest.MyPinsOperationsImpl;
import com.microsoft.socialplus.autorest.MyPushRegistrationsOperations;
import com.microsoft.socialplus.autorest.MyPushRegistrationsOperationsImpl;
import com.microsoft.socialplus.autorest.SessionsOperations;
import com.microsoft.socialplus.autorest.SessionsOperationsImpl;
import com.microsoft.socialplus.autorest.UsersOperations;
import com.microsoft.socialplus.autorest.UsersOperationsImpl;
import com.microsoft.socialplus.base.GlobalObjectRegistry;
import com.microsoft.socialplus.data.Preferences;
import com.microsoft.socialplus.sdk.Options;
import com.microsoft.socialplus.sdk.SocialPlus;

public class UserRequest extends BaseRequest {

	protected static final UsersOperations USERS;
	protected static final MyNotificationsOperations NOTIFICATIONS;
	protected static final MyPushRegistrationsOperations PUSH_REGISTRATION;
	protected static final SessionsOperations SESSION;
	protected static final MyPinsOperations PINS;
	protected static final MyBlockedUsersOperations BLOCKED;
	protected static final MyPendingUsersOperations PENDING;
	protected static final MyFollowersOperations FOLLOWERS;
	protected static final MyFollowingOperations FOLLOWING;
	protected static final HashtagsOperations HASHTAGS;
	protected static final MyLinkedAccountsOperations LINKED_ACCOUNTS;
	protected static final ImagesOperations IMAGES;

	static {
		USERS = new UsersOperationsImpl(RETROFIT, CLIENT);
		NOTIFICATIONS = new MyNotificationsOperationsImpl(RETROFIT, CLIENT);
		PUSH_REGISTRATION = new MyPushRegistrationsOperationsImpl(RETROFIT, CLIENT);
		SESSION = new SessionsOperationsImpl(RETROFIT, CLIENT);
		PINS = new MyPinsOperationsImpl(RETROFIT, CLIENT);
		BLOCKED = new MyBlockedUsersOperationsImpl(RETROFIT, CLIENT);
		PENDING = new MyPendingUsersOperationsImpl(RETROFIT, CLIENT);
		FOLLOWERS = new MyFollowersOperationsImpl(RETROFIT, CLIENT);
		FOLLOWING = new MyFollowingOperationsImpl(RETROFIT, CLIENT);
		HASHTAGS = new HashtagsOperationsImpl(RETROFIT, CLIENT);
		LINKED_ACCOUNTS = new MyLinkedAccountsOperationsImpl(RETROFIT, CLIENT);
		IMAGES = new ImagesOperationsImpl(RETROFIT, CLIENT);
	}

	//TODO: init all fields

	private String userHandle;
	protected String bearerToken;

	private long userSessionExpirationTime;
	private String userSessionSignature;

	public UserRequest() {
		userSessionSignature = "OK";
		userHandle = Preferences.getInstance().getUserHandle();
		bearerToken = Preferences.getInstance().getBearerToken();
	}

	public String getUserHandle() {
		return userHandle;
	}

	public void setUserHandle(String userHandle) {
		this.userHandle = userHandle;
	}

	public void setUserSessionSignature(String userSessionSignature) {
		this.userSessionSignature = userSessionSignature;
	}

	public void setBearerToken(String bearerToken) {
		this.bearerToken = bearerToken;
	}
}
