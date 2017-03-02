/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.event.data;

import com.microsoft.embeddedsocial.base.event.HandlingThread;
import com.microsoft.embeddedsocial.data.model.AccountData;
import com.microsoft.embeddedsocial.event.BaseUserEvent;
import com.microsoft.embeddedsocial.base.event.ThreadType;


/**
 * Profile data was loaded event.
 */
@HandlingThread(ThreadType.MAIN)
public class ProfileDataUpdatedEvent extends BaseUserEvent {

	private final AccountData accountData;

	public ProfileDataUpdatedEvent(String userHandle, AccountData accountData) {
		super(userHandle);
		this.accountData = accountData;
	}

	public AccountData getAccountData() {
		return accountData;
	}
}
