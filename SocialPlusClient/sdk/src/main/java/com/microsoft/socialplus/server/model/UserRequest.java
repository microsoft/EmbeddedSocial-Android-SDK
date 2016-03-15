/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model;

import com.microsoft.autorest.MyNotificationsOperations;
import com.microsoft.autorest.MyNotificationsOperationsImpl;
import com.microsoft.autorest.MyPinsOperations;
import com.microsoft.autorest.MyPinsOperationsImpl;
import com.microsoft.autorest.MyPushRegistrationsOperations;
import com.microsoft.autorest.MyPushRegistrationsOperationsImpl;
import com.microsoft.autorest.SessionsOperations;
import com.microsoft.autorest.SessionsOperationsImpl;
import com.microsoft.autorest.UsersOperations;
import com.microsoft.autorest.UsersOperationsImpl;
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

	static {
		USERS = new UsersOperationsImpl(RETROFIT, CLIENT);
		NOTIFICATIONS = new MyNotificationsOperationsImpl(RETROFIT, CLIENT);
		PUSH_REGISTRATION = new MyPushRegistrationsOperationsImpl(RETROFIT, CLIENT);
		SESSION = new SessionsOperationsImpl(RETROFIT, CLIENT);
		PINS = new MyPinsOperationsImpl(RETROFIT, CLIENT);
	}

	//TODO: init all fields
	private final String appHandle;

	private String userHandle;
	protected String bearerToken;

	private long userSessionExpirationTime;
	private String userSessionSignature;

	public UserRequest() {
		userSessionSignature = "OK";
		userHandle = Preferences.getInstance().getUserHandle();
		appHandle = GlobalObjectRegistry.getObject(Options.class).getAppHandle();
		bearerToken = SocialPlus.BEARER_TOKEN;
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
}
