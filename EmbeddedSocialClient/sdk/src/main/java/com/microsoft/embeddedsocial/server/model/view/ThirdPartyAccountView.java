/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model.view;

import android.text.TextUtils;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.microsoft.embeddedsocial.autorest.models.IdentityProvider;
import com.microsoft.embeddedsocial.data.storage.DbSchemas;

/**
 *
 */
@DatabaseTable(tableName = DbSchemas.ThirdPartyAccount.TABLE_NAME)
public class ThirdPartyAccountView {

	@DatabaseField(id = true, columnName = DbSchemas.ThirdPartyAccount.ACCOUNT_HANDLE)
	private String accountHandle;

	@DatabaseField
	private String thirdPartyName;

	@DatabaseField
	private String identityProvider;

	/**
	 * For ORM.
	 */
	ThirdPartyAccountView() {  }

	public ThirdPartyAccountView(String thirdPartyName, String accountHandle, IdentityProvider identityProvider) {
		this.accountHandle = accountHandle;
		this.thirdPartyName = thirdPartyName;
		this.identityProvider = identityProvider.toValue();
	}

	public String getThirdPartyName() {
		return thirdPartyName;
	}

	public IdentityProvider getIdentityProvider() {
		return IdentityProvider.fromValue(identityProvider);
	}

	public String getAccountHandle() {
		return accountHandle;
	}

	public void setAccountHandle(String accountHandle) {
		this.accountHandle = accountHandle;
	}

	public boolean hasAccountHandle() {
		return !TextUtils.isEmpty(accountHandle);
	}


}
