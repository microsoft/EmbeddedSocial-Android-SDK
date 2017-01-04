/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model;

import com.microsoft.embeddedsocial.account.UserAccount;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.exception.UnauthorizedException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.embeddedsocial.autorest.HashtagsOperations;
import com.microsoft.embeddedsocial.autorest.HashtagsOperationsImpl;
import com.microsoft.embeddedsocial.autorest.MyBlockedUsersOperations;
import com.microsoft.embeddedsocial.autorest.MyBlockedUsersOperationsImpl;
import com.microsoft.embeddedsocial.autorest.MyFollowersOperations;
import com.microsoft.embeddedsocial.autorest.MyFollowersOperationsImpl;
import com.microsoft.embeddedsocial.autorest.MyFollowingOperations;
import com.microsoft.embeddedsocial.autorest.MyFollowingOperationsImpl;
import com.microsoft.embeddedsocial.autorest.MyLikesOperations;
import com.microsoft.embeddedsocial.autorest.MyLikesOperationsImpl;
import com.microsoft.embeddedsocial.autorest.MyLinkedAccountsOperations;
import com.microsoft.embeddedsocial.autorest.MyLinkedAccountsOperationsImpl;
import com.microsoft.embeddedsocial.autorest.MyNotificationsOperations;
import com.microsoft.embeddedsocial.autorest.MyNotificationsOperationsImpl;
import com.microsoft.embeddedsocial.autorest.MyPendingUsersOperations;
import com.microsoft.embeddedsocial.autorest.MyPendingUsersOperationsImpl;
import com.microsoft.embeddedsocial.autorest.MyPinsOperations;
import com.microsoft.embeddedsocial.autorest.MyPinsOperationsImpl;
import com.microsoft.embeddedsocial.autorest.MyPushRegistrationsOperations;
import com.microsoft.embeddedsocial.autorest.MyPushRegistrationsOperationsImpl;
import com.microsoft.embeddedsocial.autorest.MyTopicsOperations;
import com.microsoft.embeddedsocial.autorest.MyTopicsOperationsImpl;
import com.microsoft.embeddedsocial.autorest.SessionsOperations;
import com.microsoft.embeddedsocial.autorest.SessionsOperationsImpl;
import com.microsoft.embeddedsocial.autorest.UsersOperations;
import com.microsoft.embeddedsocial.autorest.UsersOperationsImpl;
import com.microsoft.embeddedsocial.autorest.models.IdentityProvider;
import com.microsoft.embeddedsocial.data.Preferences;

public class UserRequest extends BaseRequest {
	public static final String ANONYMOUS = "Anon AK=%s";
	public static final String SESSION_TEMPLATE = "SocialPlus TK=%s";
	public static final String OAUTH_TEMPLATE = "%s AK=%s|TK=%s";
	public static final String TWITTER_TEMPLATE = "%s AK=%s|RT=%s|TK=%s";

	protected static final UsersOperations USERS;
	protected static final MyNotificationsOperations NOTIFICATIONS;
	protected static final MyPushRegistrationsOperations PUSH_REGISTRATION;
	protected static final SessionsOperations SESSION;
	protected static final MyPinsOperations PINS;
	protected static final MyBlockedUsersOperations BLOCKED;
	protected static final MyPendingUsersOperations PENDING;
	protected static final MyFollowersOperations MY_FOLLOWERS;
	protected static final MyFollowingOperations MY_FOLLOWING;
	protected static final HashtagsOperations HASHTAGS;
	protected static final MyLinkedAccountsOperations LINKED_ACCOUNTS;
	protected static final MyTopicsOperations MY_TOPICS;
	protected static final MyLikesOperations LIKES;

	static {
		USERS = new UsersOperationsImpl(RETROFIT, CLIENT);
		NOTIFICATIONS = new MyNotificationsOperationsImpl(RETROFIT, CLIENT);
		PUSH_REGISTRATION = new MyPushRegistrationsOperationsImpl(RETROFIT, CLIENT);
		SESSION = new SessionsOperationsImpl(RETROFIT, CLIENT);
		PINS = new MyPinsOperationsImpl(RETROFIT, CLIENT);
		BLOCKED = new MyBlockedUsersOperationsImpl(RETROFIT, CLIENT);
		PENDING = new MyPendingUsersOperationsImpl(RETROFIT, CLIENT);
		MY_FOLLOWERS = new MyFollowersOperationsImpl(RETROFIT, CLIENT);
		MY_FOLLOWING = new MyFollowingOperationsImpl(RETROFIT, CLIENT);
		HASHTAGS = new HashtagsOperationsImpl(RETROFIT, CLIENT);
		LINKED_ACCOUNTS = new MyLinkedAccountsOperationsImpl(RETROFIT, CLIENT);
		MY_TOPICS = new MyTopicsOperationsImpl(RETROFIT, CLIENT);
		LIKES = new MyLikesOperationsImpl(RETROFIT, CLIENT);
	}

	//TODO: init all fields

	private String userHandle;
	protected String authorization;

	private long userSessionExpirationTime;
	private String userSessionSignature;

	public UserRequest() {
		userSessionSignature = "OK";
		userHandle = Preferences.getInstance().getUserHandle();
		authorization = Preferences.getInstance().getAuthorizationToken();

		if (authorization == null) {
			authorization = String.format(ANONYMOUS, appKey);
			Preferences.getInstance().setAuthorizationToken(authorization);
		}
	}

	public static String createSessionAuthorization(String sessionToken) {
		return String.format(SESSION_TEMPLATE, sessionToken);
	}

	public String createThirdPartyAuthorization(IdentityProvider identityProvider,
												String accessToken, String requestToken) {
		if (identityProvider == IdentityProvider.TWITTER) {
			return String.format(TWITTER_TEMPLATE, identityProvider, appKey, requestToken, accessToken);
		}
		return String.format(OAUTH_TEMPLATE, identityProvider, appKey, accessToken);
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

	public String getAuthorization() {
		return authorization;
	}

	@Override
	protected void checkResponseCode(ServiceResponse serviceResponse) throws NetworkRequestException {
		// TODO
		switch (serviceResponse.getResponse().code()) {
			case 401: // unauthorized
				// invalidate session token
				UserAccount.getInstance().signOut();
				throw new UnauthorizedException(serviceResponse.getResponse().message());
			default:
				super.checkResponseCode(serviceResponse);
		}
	}

	public void setAuthorization(String authorization) {
		this.authorization = authorization;
	}
}
