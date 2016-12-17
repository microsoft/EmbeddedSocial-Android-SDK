/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model.view;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.microsoft.embeddedsocial.data.storage.DbSchemas;
import com.microsoft.embeddedsocial.autorest.models.*;
import com.microsoft.embeddedsocial.server.model.UniqueItem;

import java.util.ArrayList;
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
	private String firstName;

	@DatabaseField
	private String lastName;

	@DatabaseField
	private String userPhotoUrl;

	@DatabaseField
	private String bio;

	@DatabaseField
	private boolean isPrivate;

	private List<ThirdPartyAccountView> thirdPartyAccounts;

	/**
	 * For ORM.
	 */
	UserAccountView() {  }

	public UserAccountView(com.microsoft.embeddedsocial.autorest.models.UserProfileView profileView,
						   List<LinkedAccountView> linkedAccounts) {
		userHandle = profileView.getUserHandle();
		firstName = profileView.getFirstName();
		lastName = profileView.getLastName();
		userPhotoUrl = profileView.getPhotoUrl();
		bio = profileView.getBio();
		isPrivate = profileView.getVisibility() == Visibility.PRIVATE;
		thirdPartyAccounts = new ArrayList<>();
		for (LinkedAccountView linkedAccount : linkedAccounts) {
			IdentityProvider provider = linkedAccount.getIdentityProvider();
			thirdPartyAccounts.add(
					new ThirdPartyAccountView(provider.name(), linkedAccount.getAccountId(), provider));
		}
	}

	@Override
	public String getHandle() {
		return userHandle;
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

	public boolean isPrivate() {
		return isPrivate;
	}

	public void setThirdPartyAccounts(List<ThirdPartyAccountView> thirdPartyAccounts) {
		this.thirdPartyAccounts = thirdPartyAccounts;
	}

	public List<ThirdPartyAccountView> getThirdPartyAccounts() {
		return thirdPartyAccounts != null ? thirdPartyAccounts : Collections.emptyList();
	}
}
