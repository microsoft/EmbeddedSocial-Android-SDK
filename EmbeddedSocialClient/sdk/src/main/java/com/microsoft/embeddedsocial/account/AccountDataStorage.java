/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.account;

import com.microsoft.embeddedsocial.autorest.models.IdentityProvider;
import com.microsoft.embeddedsocial.data.model.AccountData;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Stores {@link AccountData} permanently.
 */
final class AccountDataStorage {

	private static final String PREFS_FILENAME = "account";
	private static final String FIRST_NAME = "firstName";
	private static final String LAST_NAME = "lastName";
	private static final String USERNAME = "username";
	private static final String BIO = "bio";
	private static final String FOLLOWERS_COUNT = "followersCount";
	private static final String FOLLOWING_COUNT = "followingCount";
	private static final String PRIVATE = "private";
	private static final String IDENTITY_PROVIDER = "accountType";
	private static final String PHOTO_URL = "photoUrl";
	private static final String EMAIL = "email";
	private static final String DEVICE_ACCOUNT = "device_account";

	private AccountDataStorage() {
	}

	/**
	 * Saves current user's account data to the disk.
	 *
	 * @param context     context
	 * @param accountData current user's account data
	 */
	static void store(Context context, AccountData accountData) {
		SharedPreferences prefs = getPrefs(context);
		prefs.edit()
				.putString(FIRST_NAME, accountData.getFirstName())
				.putString(LAST_NAME, accountData.getLastName())
				.putString(BIO, accountData.getBio())
				.putLong(FOLLOWERS_COUNT, accountData.getFollowersCount())
				.putLong(FOLLOWING_COUNT, accountData.getFollowingCount())
				.putBoolean(PRIVATE, accountData.isPrivate())
				.putString(IDENTITY_PROVIDER, accountData.getIdentityProvider().toValue())
				.putString(PHOTO_URL, accountData.getUserPhotoUrl())
			.apply();
	}

	/**
	 * Saves the current user's hashed email to the disk.
	 * @param context runtime context
	 * @param email hash of email
	 */
	static void storeEmail(Context context, String email) {
		SharedPreferences prefs = getPrefs(context);
		prefs.edit()
				.putString(EMAIL, email)
			.apply();
	}

	/**
	 * Saves to disk whether or not the user account used to authenticate is a device account
	 * @param context runtime context
	 * @param isDeviceAccount true if the account is a device account, false otherwise
	 */
	static void storeIsDeviceAccount(Context context, boolean isDeviceAccount) {
		SharedPreferences prefs = getPrefs(context);
		prefs.edit()
				.putBoolean(DEVICE_ACCOUNT, isDeviceAccount)
			.apply();
	}


	/**
	 * Returns the user's account data previously stored on the disk
	 * @param context context
	 */
	static AccountData get(Context context) {
		SharedPreferences prefs = getPrefs(context);
		AccountData accountData = new AccountData();
		accountData.setFirstName(prefs.getString(FIRST_NAME, ""));
		accountData.setLastName(prefs.getString(LAST_NAME, ""));
		accountData.setBio(prefs.getString(BIO, ""));
		accountData.setFollowersCount(prefs.getLong(FOLLOWERS_COUNT, 0));
		accountData.setFollowingCount(prefs.getLong(FOLLOWING_COUNT, 0));
		accountData.setIsPrivate(prefs.getBoolean(PRIVATE, false));
		accountData.setIdentityProvider(IdentityProvider.fromValue(prefs.getString(IDENTITY_PROVIDER, "")));
		accountData.setUserPhotoUrl(prefs.getString(PHOTO_URL, ""));
		accountData.setEmail(prefs.getString(EMAIL, ""));
		accountData.setIsDeviceAccount(prefs.getBoolean(DEVICE_ACCOUNT, false));
		return accountData;
	}

	/**
	 * Clears the user's account data stored on the disk
	 * @param context context
	 */
	static void clear(Context context) {
		getPrefs(context).edit().clear().apply();
	}

	private static SharedPreferences getPrefs(Context context) {
		return context.getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE);
	}

}
