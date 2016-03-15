/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.view;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.microsoft.socialplus.data.storage.DbSchemas;
import com.microsoft.socialplus.server.model.UniqueItem;

import java.util.Collections;
import java.util.List;

/**
 *
 */
@DatabaseTable(tableName = DbSchemas.UserAccount.TABLE_NAME)
public class UserAccountView implements UniqueItem {

	@DatabaseField(id = true, columnName = DbSchemas.UserFeeds.USER_HANDLE)
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
	private String userWebsite;

	@DatabaseField
	private boolean isPrivate;

	@DatabaseField
	private String email;

	@DatabaseField
	private String phoneNumber;

	@DatabaseField
	private int gender;

	@DatabaseField
	private long birthday;

	@DatabaseField
	private boolean hasPassword;

	private List<ThirdPartyAccountView> thirdPartyAccounts;

	/**
	 * For ORM.
	 */
	UserAccountView() {  }

	@Override
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

	public String getBio() {
		return bio;
	}

	public String getUserWebsite() {
		return userWebsite;
	}

	public boolean isPrivate() {
		return isPrivate;
	}

	public String getEmail() {
		return email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public int getGender() {
		return gender;
	}

	public long getBirthday() {
		return birthday;
	}

	public boolean isHasPassword() {
		return hasPassword;
	}

	public void setThirdPartyAccounts(List<ThirdPartyAccountView> thirdPartyAccounts) {
		this.thirdPartyAccounts = thirdPartyAccounts;
	}

	public List<ThirdPartyAccountView> getThirdPartyAccounts() {
		return thirdPartyAccounts != null ? thirdPartyAccounts : Collections.emptyList();
	}
}
